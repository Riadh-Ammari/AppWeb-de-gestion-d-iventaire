package com.example.StockService.service;

import com.example.StockService.dto.StockRequest;
import com.example.StockService.dto.StockResponse;
import com.example.StockService.model.Stock;
import com.example.StockService.repository.StockRepository;
import com.example.StockService.service.exeception.StockNotFoundException;
import com.example.StockService.dto.ClientInfo;
import com.example.StockService.dto.FournisseurInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockRepository stockRepository;
    private final WebClient.Builder webClientBuilder;

    public StockResponse createStock(StockRequest stockRequest) {
        Stock stock = Stock.builder()
                .capacite(stockRequest.getCapacite())
                .quantiteTotalStocke(stockRequest.getQuantiteTotalStocke())
                .nom(stockRequest.getNom())
                .adresse(stockRequest.getAdresse())
                .etat(stockRequest.getEtat())
                .produits(Optional.ofNullable(stockRequest.getProduits()).orElse(new HashMap<>()))
                .historiqueCommandes(Optional.ofNullable(stockRequest.getHistoriqueCommandes()).orElse(new ArrayList<>()))
                .fournisseurs(Optional.ofNullable(stockRequest.getFournisseurs()).orElse(new ArrayList<>()))
                .clients(Optional.ofNullable(stockRequest.getClients()).orElse(new ArrayList<>()))
                .build();

        stock = stockRepository.save(stock);
        log.info("Stock {} has been saved.", stock.getIdStock());
        return mapToStockResponse(stock);
    }

    public StockResponse updateStock(String id, StockRequest stockRequest) {
        Stock existingStock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException("Cannot find stock with id: " + id));

        existingStock.setNom(stockRequest.getNom());
        existingStock.setEtat(stockRequest.getEtat());
        existingStock.setAdresse(stockRequest.getAdresse());
        existingStock.setCapacite(stockRequest.getCapacite());
        existingStock.setQuantiteTotalStocke(stockRequest.getQuantiteTotalStocke());

        Stock updatedStock = stockRepository.save(existingStock);
        return mapToStockResponse(updatedStock);
    }

    public List<StockResponse> getAllStocks() {
        return stockRepository.findAll()
                .stream()
                .map(this::mapToStockResponse)
                .toList();
    }

    public StockResponse getStockByName(String nom) {
        Stock stock = stockRepository.findByNom(nom)
                .orElseThrow(() -> new StockNotFoundException("Cannot find stock with name: " + nom));
        return mapToStockResponse(stock);
    }

    public StockResponse getStockById(String idStock) {
        Stock stock = stockRepository.findById(idStock)
                .orElseThrow(() -> new StockNotFoundException("Stock not found with id: " + idStock));
        return mapToStockResponse(stock);
    }

    public void deleteStock(String id) {
        stockRepository.deleteById(id);
        log.info("Stock with id {} has been deleted.", id);
    }

    public void addClientToStock(String stockId, String clientId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found"));

        if (stock.getClients() == null) {
            stock.setClients(new ArrayList<>());
        }

        if (!stock.getClients().contains(clientId)) {
            stock.getClients().add(clientId);
            stockRepository.save(stock);
        }
    }

    public void addFournisseurToStock(String stockId, String fournisseurId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found"));

        if (stock.getFournisseurs() == null) {
            stock.setFournisseurs(new ArrayList<>());
        }

        if (!stock.getFournisseurs().contains(fournisseurId)) {
            stock.getFournisseurs().add(fournisseurId);
            stockRepository.save(stock);
        }
    }

    public String getStockIdByProductId(String productId) {
        Stock stock = stockRepository.findAll().stream()
                .filter(s -> s.getProduits() != null && s.getProduits().containsKey(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Stock not found for product with ID: " + productId));

        return stock.getIdStock();
    }

    public void addCommandeToStock(String stockId, String commandeId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found"));

        if (stock.getHistoriqueCommandes() == null) {
            stock.setHistoriqueCommandes(new ArrayList<>());
        }

        if (!stock.getHistoriqueCommandes().contains(commandeId)) {
            stock.getHistoriqueCommandes().add(commandeId);
            stockRepository.save(stock);
        }
    }

    public void addProductToStock(String stockId, String productId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found"));

        if (stock.getProduits() == null) {
            stock.setProduits(new HashMap<>());
        }

        stock.getProduits().put(productId, stock.getProduits().getOrDefault(productId, 0));
        stockRepository.save(stock);
    }

    public void addQuantiteToProduit(String stockId, String productId, int quantiteToAdd) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        Map<String, Integer> produits = stock.getProduits();
        if (produits == null) {
            produits = new HashMap<>();
        }

        int currentTotal = stock.getQuantiteTotalStocke() != null ? stock.getQuantiteTotalStocke() : 0;
        int capaciteMax = stock.getCapacite() != null ? stock.getCapacite() : Integer.MAX_VALUE;
        if (currentTotal + quantiteToAdd > capaciteMax) {
            throw new RuntimeException("Ajout impossible : capacité du stock dépassée !");
        }

        int updatedProductQuantite = produits.getOrDefault(productId, 0) + quantiteToAdd;
        produits.put(productId, updatedProductQuantite);
        stock.setProduits(produits);

        stock.setQuantiteTotalStocke(currentTotal + quantiteToAdd);
        stockRepository.save(stock);
    }

    public void decreaseQuantiteToProduit(String stockId, String productId, int quantiteToRemove) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        Map<String, Integer> produits = stock.getProduits();
        if (produits == null || !produits.containsKey(productId)) {
            throw new RuntimeException("Product not found in stock.");
        }

        int currentProductQuantity = produits.get(productId);
        if (currentProductQuantity < quantiteToRemove) {
            throw new RuntimeException("Insufficient stock quantity for product.");
        }

        int updatedQuantity = currentProductQuantity - quantiteToRemove;
        if (updatedQuantity == 0) {
            produits.remove(productId);
        } else {
            produits.put(productId, updatedQuantity);
        }

        stock.setProduits(produits);
        int currentTotal = stock.getQuantiteTotalStocke() != null ? stock.getQuantiteTotalStocke() : 0;
        int newTotal = currentTotal - quantiteToRemove;
        stock.setQuantiteTotalStocke(Math.max(newTotal, 0));
        stockRepository.save(stock);
    }

    public void removeProductFromStock(String stockId, String productId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found"));

        if (stock.getProduits() != null) {
            stock.getProduits().remove(productId);
        }

        stockRepository.save(stock);
    }

    public boolean isStockAvailable(Map<String, Integer> produits) {
        for (Map.Entry<String, Integer> entry : produits.entrySet()) {
            String productId = entry.getKey();
            int requestedQuantity = entry.getValue();

            Stock stock = stockRepository.findAll().stream()
                    .filter(s -> s.getProduits().containsKey(productId))
                    .findFirst()
                    .orElse(null);

            if (stock == null || stock.getProduits().get(productId) < requestedQuantity) {
                return false;
            }
        }
        return true;
    }

    public List<ClientInfo> getClientsByIdStock(String stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found with id: " + stockId));

        List<String> clientIds = stock.getClients() != null ? stock.getClients() : new ArrayList<>();
        if (clientIds.isEmpty()) {
            return new ArrayList<>();
        }

        return clientIds.stream()
                .map(clientId -> {
                    try {
                        Map<String, String> response = webClientBuilder.build()
                                .get()
                                .uri("http://CLIENTSERVICE/api/client/id/{idClient}", clientId)
                                .retrieve()
                                .bodyToMono(Map.class)
                                .block();
                        if (response != null && response.containsKey("idClient") && response.containsKey("nom")) {
                            return ClientInfo.builder()
                                    .idClient(response.get("idClient"))
                                    .nom(response.get("nom"))
                                    .build();
                        }
                        log.warn("Invalid client response for id: {}", clientId);
                        return null;
                    } catch (WebClientResponseException e) {
                        log.error("Error fetching client for id: {}. Status: {}, Body: {}",
                                clientId, e.getStatusCode(), e.getResponseBodyAsString(), e);
                        return null;
                    } catch (Exception e) {
                        log.error("Unexpected error fetching client for id: {}", clientId, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<FournisseurInfo> getFournisseursByIdStock(String stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found with id: " + stockId));

        List<String> fournisseurIds = stock.getFournisseurs() != null ? stock.getFournisseurs() : new ArrayList<>();
        if (fournisseurIds.isEmpty()) {
            log.info("No fournisseur IDs found for stockId: {}", stockId);
            return new ArrayList<>();
        }

        return fournisseurIds.stream()
                .map(fournisseurId -> {
                    try {
                        Map<String, String> response = webClientBuilder.build()
                                .get()
                                .uri("http://FOURNISSEURSERVICE/api/fournisseur/id/{idFournisseur}", fournisseurId)
                                .retrieve()
                                .bodyToMono(Map.class)
                                .block();
                        if (response != null && response.containsKey("idFournisseur") && response.containsKey("nom")) {
                            return FournisseurInfo.builder()
                                    .idFournisseur(response.get("idFournisseur"))
                                    .nom(response.get("nom"))
                                    .build();
                        }
                        log.warn("Invalid fournisseur response for id: {}", fournisseurId);
                        return null;
                    } catch (WebClientResponseException e) {
                        log.error("Error fetching fournisseur for id: {}. Status: {}, Body: {}",
                                fournisseurId, e.getStatusCode(), e.getResponseBodyAsString(), e);
                        return null;
                    } catch (Exception e) {
                        log.error("Unexpected error fetching fournisseur for id: {}", fournisseurId, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private StockResponse mapToStockResponse(Stock stock) {
        return StockResponse.builder()
                .idStock(stock.getIdStock())
                .capacite(stock.getCapacite())
                .quantiteTotalStocke(stock.getQuantiteTotalStocke())
                .nom(stock.getNom())
                .adresse(stock.getAdresse())
                .etat(stock.getEtat())
                .produits(stock.getProduits())
                .historiqueCommandes(stock.getHistoriqueCommandes())
                .fournisseurs(stock.getFournisseurs())
                .clients(stock.getClients())
                .build();
    }
}