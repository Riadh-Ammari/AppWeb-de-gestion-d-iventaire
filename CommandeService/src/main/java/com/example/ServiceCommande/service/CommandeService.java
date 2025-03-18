package com.example.ServiceCommande.service;

import com.example.ServiceCommande.dto.CommandeRequest;
import com.example.ServiceCommande.dto.CommandeResponse;
import com.example.ServiceCommande.model.Commande;
import com.example.ServiceCommande.repository.CommandeRepository;
import com.example.ServiceCommande.service.exception.CommandeNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandeService {

    private final CommandeRepository commandeRepository;

    // Création d'une nouvelle commande
    public CommandeResponse createCommande(CommandeRequest commandeRequest) {
        Commande commande = Commande.builder()
                .dateCommande(commandeRequest.getDateCommande())
                .montantTotal(commandeRequest.getMontantTotal())
                .destination(commandeRequest.getDestination())
                .etat(commandeRequest.getEtat())
                .produits(commandeRequest.getProduits())
                .fournisseur(commandeRequest.getFournisseur())
                .build();

        commande = commandeRepository.save(commande);
        log.info("Commande {} a été enregistrée.", commande.getIdCommande());
        return mapToCommandeResponse(commande);
    }

    // Mise à jour d'une commande existante
    public CommandeResponse updateCommande(String id, CommandeRequest commandeRequest) {
        Commande existingCommande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Impossible de trouver la commande avec l'ID %s", id)));

        existingCommande.setDateCommande(commandeRequest.getDateCommande());
        existingCommande.setMontantTotal(commandeRequest.getMontantTotal());
        existingCommande.setDestination(commandeRequest.getDestination());
        existingCommande.setEtat(commandeRequest.getEtat());
        existingCommande.setProduits(commandeRequest.getProduits());
        existingCommande.setFournisseur(commandeRequest.getFournisseur());

        Commande updatedCommande = commandeRepository.save(existingCommande);
        return mapToCommandeResponse(updatedCommande);
    }

    // Récupérer toutes les commandes
    public List<CommandeResponse> getAllCommandes() {
        List<Commande> commandes = commandeRepository.findAll();
        return commandes.stream().map(this::mapToCommandeResponse).toList();
    }

    // Récupérer une commande par son ID
    public CommandeResponse getCommandeById(String idCommande) {
        // Recherche de la commande par ID dans la base de données
        Optional<Commande> commande = commandeRepository.findById(idCommande);

        // Si la commande est trouvée, on construit la réponse
        if (commande.isPresent()) {
            Commande c = commande.get();
            return CommandeResponse.builder()
                    .idCommande(c.getIdCommande())
                    .dateCommande(c.getDateCommande())
                    .montantTotal(c.getMontantTotal())
                    .destination(c.getDestination())
                    .etat(c.getEtat())
                    .produits(c.getProduits()) // Liste des IDs des produits
                    .fournisseur(c.getFournisseur())
                    .build();
        } else {
            // Si la commande n'est pas trouvée, on lance une exception personnalisée
            throw new CommandeNotFoundException("Commande introuvable avec l'ID : " + idCommande);
        }
    }

    public List<CommandeResponse> getCommandesByEtat(String etat) {
        List<Commande> commandes = commandeRepository.findByEtat(etat);
        return commandes.stream().map(this::mapToCommandeResponse).toList();
    }

    // Récupérer les commandes par fournisseur
    public List<CommandeResponse> getCommandesByFournisseur(String fournisseur) {
        List<Commande> commandes = commandeRepository.findByFournisseur(fournisseur);
        return commandes.stream().map(this::mapToCommandeResponse).toList();
    }

    // Récupérer les commandes par destination
    public List<CommandeResponse> getCommandesByDestination(String destination) {
        List<Commande> commandes = commandeRepository.findByDestination(destination);
        return commandes.stream().map(this::mapToCommandeResponse).toList();
    }


    // Supprimer une commande par ID
    public void deleteCommande(String id) {
        commandeRepository.deleteById(id);
        log.info("Commande avec l'ID {} a été supprimée.", id);
    }

    // Mapper un objet Commande vers CommandeResponse
    private CommandeResponse mapToCommandeResponse(Commande commande) {
        return CommandeResponse.builder()
                .idCommande(commande.getIdCommande())
                .dateCommande(commande.getDateCommande())
                .montantTotal(commande.getMontantTotal())
                .destination(commande.getDestination())
                .etat(commande.getEtat())
                .produits(commande.getProduits())
                .fournisseur(commande.getFournisseur())
                .build();
    }
}
