package com.example.FactureService.kafka;

import com.example.FactureService.dto.FactureRequest;
import com.example.FactureService.service.FactureService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import primitives.Commande;
import primitives.OrderEvent;

import java.util.Date;
import java.util.HashMap;

@Service
public class CommandeConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandeConsumer.class);
    private final FactureService factureService;
    public CommandeConsumer(FactureService factureService) {
        this.factureService = factureService;
    }
    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(OrderEvent event){
        try {
            LOGGER.info("✅ Order event received in FactureService: {}", event.toString());

            // Extraire la commande depuis l'event
            Commande commande = event.getOrdre();

            // Construire la facture
            FactureRequest factureRequest = FactureRequest.builder()
                    .idCommande(commande.getIdCommande())
                    .idClient(commande.getIdClient())
                    .idFournisseur(commande.getIdFournisseur())
                    .montantTotal(commande.getMontantTotal())
                    .produits(commande.getProduits())
                    .produitsAdditionnels(new HashMap<>())
                    .etat("Non Payée")
                    .dateFacture(commande.getDateCommande())
                    .build();

            factureService.createFacture(factureRequest);
            LOGGER.info("🧾 Facture créée automatiquement pour la commande {}", commande.getIdCommande());
        } catch (Exception e) {
            LOGGER.error("❌ Error processing order event: {}", event, e);
        }
    }


}