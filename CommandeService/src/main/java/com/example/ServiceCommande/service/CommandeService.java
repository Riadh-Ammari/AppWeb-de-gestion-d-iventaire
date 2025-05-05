package com.example.ServiceCommande.service;

import com.example.ServiceCommande.dto.ClientInfo;
import com.example.ServiceCommande.dto.CommandeRequest;
import com.example.ServiceCommande.dto.CommandeResponse;
import com.example.ServiceCommande.dto.MonthlyOrdersResponse;
import com.example.ServiceCommande.kafka.CommandeProducer;
import com.example.ServiceCommande.repository.CommandeRepository;
import com.example.ServiceCommande.service.exception.CommandeNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import primitives.Commande;
import primitives.OrderEvent;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final WebClient.Builder webClientBuilder;
    private final CommandeProducer commandeProducer;

    public CommandeResponse createCommande(CommandeRequest commandeRequest) {
        Map<String, Integer> produits = commandeRequest.getProduits();

        if (produits.isEmpty()) {
            throw new RuntimeException("La commande ne contient aucun produit.");
        }

        String firstProductId = produits.keySet().iterator().next();
        String stockId = webClientBuilder.build()
                .get()
                .uri("http://STOCKSERVICE/api/stock/produit/{productId}/stock-id", firstProductId)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> response.get("stockId").toString())
                .block();

        if (stockId == null) {
            throw new RuntimeException("Produit introuvable ou sans stock: " + firstProductId);
        }

        for (Map.Entry<String, Integer> entry : produits.entrySet()) {
            String productId = entry.getKey();
            Integer quantity = entry.getValue();

            Map<String, Integer> checkMap = new HashMap<>();
            checkMap.put(productId, quantity);

            Boolean isAvailable = webClientBuilder.build()
                    .post()
                    .uri("http://STOCKSERVICE/api/stock/check")
                    .bodyValue(checkMap)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            if (isAvailable == null || !isAvailable) {
                throw new RuntimeException("Stock insuffisant pour le produit: " + productId);
            }

            webClientBuilder.build()
                    .put()
                    .uri("http://STOCKSERVICE/api/stock/{stockId}/decrease-quantity/{productId}/{quantity}", stockId, productId, quantity)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        }

        // Fetch client address for delivery
        String deliveryAddress = webClientBuilder.build()
                .get()
                .uri("http://CLIENTSERVICE/api/client/id/{idClient}", commandeRequest.getIdClient())
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> response.get("adresse").toString())
                .block();

        if (deliveryAddress == null) {
            throw new RuntimeException("Adresse du client introuvable pour idClient: " + commandeRequest.getIdClient());
        }

        Commande commande = Commande.builder()
                .dateCommande(commandeRequest.getDateCommande())
                .montantTotal(commandeRequest.getMontantTotal())
                .idClient(commandeRequest.getIdClient())
                .etat("En Cours")
                .produits(commandeRequest.getProduits())
                .idFournisseur(commandeRequest.getIdFournisseur())
                .build();

        commande = commandeRepository.save(commande);
        log.info("Commande {} a été enregistrée.", commande.getIdCommande());

        // Update fournisseur availability and delivery address
        try {
            webClientBuilder.build()
                    .put()
                    .uri("http://FOURNISSEURSERVICE/api/fournisseur/{id}/availability", commande.getIdFournisseur())
                    .bodyValue(Map.of(
                            "availability", "occupé",
                            "deliveryAddresses", List.of(deliveryAddress)
                    ))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            log.info("Fournisseur {} set to occupé with delivery address {}", commande.getIdFournisseur(), deliveryAddress);
        } catch (Exception e) {
            log.error("Error updating fournisseur availability for id: {}. {}", commande.getIdFournisseur(), e.getMessage(), e);
        }

        OrderEvent event = OrderEvent.builder()
                .ordre(commande)
                .message("Nouvelle commande créée avec succès !")
                .build();
        commandeProducer.envoyerMessage(event);

        try {
            webClientBuilder.build()
                    .put()
                    .uri("http://CLIENTSERVICE/api/client/{idClient}/addCommande/{idCommande}", commande.getIdClient(), commande.getIdCommande())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            log.info("Commande {} ajoutée au client {}", commande.getIdCommande(), commande.getIdClient());
        } catch (WebClientResponseException e) {
            log.error("Erreur lors de l'ajout de la commande au client: {}. Status: {}, Body: {}",
                    commande.getIdCommande(), e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'ajout de la commande au client: {}", commande.getIdCommande(), e);
        }

        try {
            webClientBuilder.build()
                    .put()
                    .uri("http://STOCKSERVICE/api/stock/{stockId}/add-commande/{idCommande}", stockId, commande.getIdCommande())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            log.info("Commande {} ajoutée à l'historique des commandes du stock {}", commande.getIdCommande(), stockId);
        } catch (WebClientResponseException e) {
            log.error("Erreur lors de l'ajout de la commande au stock: {}. Status: {}, Body: {}",
                    commande.getIdCommande(), e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'ajout de la commande au stock: {}", commande.getIdCommande(), e);
        }

        return mapToCommandeResponse(commande);
    }

    public CommandeResponse updateCommande(String id, CommandeRequest commandeRequest) {
        Commande existingCommande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Impossible de trouver la commande avec l'ID %s", id)));

        String oldEtat = existingCommande.getEtat();
        String newEtat = commandeRequest.getEtat();

        existingCommande.setDateCommande(commandeRequest.getDateCommande());
        existingCommande.setMontantTotal(commandeRequest.getMontantTotal());
        existingCommande.setIdClient(commandeRequest.getIdClient());
        existingCommande.setEtat(newEtat);
        existingCommande.setProduits(commandeRequest.getProduits());
        existingCommande.setIdFournisseur(commandeRequest.getIdFournisseur());

        Commande updatedCommande = commandeRepository.save(existingCommande);

        // Update fournisseur availability if order is marked as "livrée"
        if (!"livrée".equals(oldEtat) && "livrée".equals(newEtat) && updatedCommande.getIdFournisseur() != null) {
            try {
                webClientBuilder.build()
                        .put()
                        .uri("http://FOURNISSEURSERVICE/api/fournisseur/{id}/availability", updatedCommande.getIdFournisseur())
                        .bodyValue(Map.of(
                                "availability", "libre",
                                "deliveryAddresses", new ArrayList<>()
                        ))
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block();
                log.info("Fournisseur {} set to libre with empty delivery addresses", updatedCommande.getIdFournisseur());
            } catch (Exception e) {
                log.error("Error updating fournisseur availability for id: {}. {}", updatedCommande.getIdFournisseur(), e.getMessage(), e);
            }
        }

        return mapToCommandeResponse(updatedCommande);
    }

    public ClientInfo getClientByIdCommande(String commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> {
                    log.error("Commande not found with id: {}", commandeId);
                    return new CommandeNotFoundException("Commande not found with id: " + commandeId);
                });

        String idClient = commande.getIdClient();
        if (idClient == null || idClient.isEmpty()) {
            log.error("No client associated with commande: {}", commandeId);
            throw new RuntimeException("No client associated with commande: " + commandeId);
        }

        try {
            Map<String, String> clientResponse = webClientBuilder.build()
                    .get()
                    .uri("http://CLIENTSERVICE/api/client/id/{idClient}", idClient)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (clientResponse == null || !clientResponse.containsKey("idClient") || !clientResponse.containsKey("nom")) {
                log.error("Invalid client response for id: {}", idClient);
                throw new RuntimeException("Client not found for id: " + idClient);
            }

            return ClientInfo.builder()
                    .idClient(clientResponse.get("idClient"))
                    .nom(clientResponse.get("nom"))
                    .build();
        } catch (WebClientResponseException e) {
            log.error("Error fetching client for id: {}. Status: {}, Body: {}",
                    idClient, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to fetch client: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching client for id: {}", idClient, e);
            throw new RuntimeException("Unexpected error fetching client: " + e.getMessage());
        }
    }

    public List<CommandeResponse> getAllCommandes() {
        List<Commande> commandes = commandeRepository.findAll();
        return commandes.stream().map(this::mapToCommandeResponse).toList();
    }

    public CommandeResponse getCommandeById(String idCommande) {
        Optional<Commande> commande = commandeRepository.findById(idCommande);
        if (commande.isPresent()) {
            Commande c = commande.get();
            return CommandeResponse.builder()
                    .idCommande(c.getIdCommande())
                    .dateCommande(c.getDateCommande())
                    .montantTotal(c.getMontantTotal())
                    .idClient(c.getIdClient())
                    .etat(c.getEtat())
                    .produits(c.getProduits())
                    .idFournisseur(c.getIdFournisseur())
                    .build();
        } else {
            throw new CommandeNotFoundException("Commande introuvable avec l'ID : " + idCommande);
        }
    }

    public List<CommandeResponse> getCommandesByEtat(String etat) {
        List<Commande> commandes = commandeRepository.findByEtat(etat);
        return commandes.stream().map(this::mapToCommandeResponse).toList();
    }

    public List<CommandeResponse> getCommandesByFournisseur(String fournisseur) {
        List<Commande> commandes = commandeRepository.findByFournisseur(fournisseur);
        return commandes.stream().map(this::mapToCommandeResponse).toList();
    }

    public List<CommandeResponse> getCommandesByDestination(String destination) {
        List<Commande> commandes = commandeRepository.findByDestination(destination);
        return commandes.stream().map(this::mapToCommandeResponse).toList();
    }

    public void deleteCommande(String id) {
        String IdFacture = webClientBuilder.build()
                .get()
                .uri("http://FACTURESERVICE/api/factures/Commande/{idCommande}", id)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (IdFacture != null) {
            try {
                webClientBuilder.build()
                        .delete()
                        .uri("http://FACTURESERVICE/api/factures/{id}", IdFacture)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block();
                log.info("Facture {} removed for commande {}", IdFacture, id);
            } catch (WebClientResponseException e) {
                log.error("Failed to remove facture {}: Status: {}, Body: {}",
                        IdFacture, e.getStatusCode(), e.getResponseBodyAsString(), e);
                throw new RuntimeException("Failed to remove facture: " + e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error removing facture {}: {}", IdFacture, e);
                throw new RuntimeException("Failed to remove facture: " + e.getMessage());
            }
        }

        commandeRepository.deleteById(id);
        log.info("Commande avec l'ID {} a été supprimée.", id);
    }

    public Double getTotalRevenue() {
        List<Commande> commandes = commandeRepository.findAll();
        double totalRevenue = commandes.stream()
                .filter(commande -> commande.getMontantTotal() != null)
                .mapToDouble(Commande::getMontantTotal)
                .sum();
        log.info("Total revenue calculated: {}", totalRevenue);
        return totalRevenue;
    }

    public List<MonthlyOrdersResponse> getOrdersByMonth() {
        List<Commande> commandes = commandeRepository.findAll();
        Map<String, List<Commande>> ordersByMonth = commandes.stream()
                .filter(commande -> commande.getDateCommande() != null)
                .collect(Collectors.groupingBy(
                        commande -> {
                            LocalDate localDate = commande.getDateCommande().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            return localDate.getYear() + "-" + String.format("%02d", localDate.getMonthValue());
                        }
                ));

        return ordersByMonth.entrySet().stream()
                .map(entry -> {
                    String[] yearMonth = entry.getKey().split("-");
                    int year = Integer.parseInt(yearMonth[0]);
                    int month = Integer.parseInt(yearMonth[1]);
                    List<CommandeResponse> orders = entry.getValue().stream()
                            .map(this::mapToCommandeResponse)
                            .toList();
                    return MonthlyOrdersResponse.builder()
                            .year(year)
                            .month(month)
                            .orders(orders)
                            .build();
                })
                .sorted(Comparator.comparing(MonthlyOrdersResponse::getYear)
                        .thenComparing(MonthlyOrdersResponse::getMonth))
                .toList();
    }

    public List<MonthlyOrdersResponse> getDeliveredOrdersByMonth() {
        List<Commande> commandes = commandeRepository.findByEtat("livrée");
        Map<String, List<Commande>> deliveredOrdersByMonth = commandes.stream()
                .filter(commande -> commande.getDateCommande() != null)
                .collect(Collectors.groupingBy(
                        commande -> {
                            LocalDate localDate = commande.getDateCommande().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            return localDate.getYear() + "-" + String.format("%02d", localDate.getMonthValue());
                        }
                ));

        return deliveredOrdersByMonth.entrySet().stream()
                .map(entry -> {
                    String[] yearMonth = entry.getKey().split("-");
                    int year = Integer.parseInt(yearMonth[0]);
                    int month = Integer.parseInt(yearMonth[1]);
                    List<CommandeResponse> orders = entry.getValue().stream()
                            .map(this::mapToCommandeResponse)
                            .toList();
                    return MonthlyOrdersResponse.builder()
                            .year(year)
                            .month(month)
                            .orders(orders)
                            .build();
                })
                .sorted(Comparator.comparing(MonthlyOrdersResponse::getYear)
                        .thenComparing(MonthlyOrdersResponse::getMonth))
                .toList();
    }

    public List<CommandeResponse> getPendingDeliveryOrders() {
        List<Commande> pendingOrders = commandeRepository.findPendingDelivery();
        return pendingOrders.stream()
                .map(this::mapToCommandeResponse)
                .toList();
    }

    private CommandeResponse mapToCommandeResponse(Commande commande) {
        return CommandeResponse.builder()
                .idCommande(commande.getIdCommande())
                .dateCommande(commande.getDateCommande())
                .montantTotal(commande.getMontantTotal())
                .idClient(commande.getIdClient())
                .etat(commande.getEtat())
                .produits(commande.getProduits())
                .idFournisseur(commande.getIdFournisseur())
                .build();
    }
}