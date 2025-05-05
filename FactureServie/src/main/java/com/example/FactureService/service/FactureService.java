package com.example.FactureService.service;

import com.example.FactureService.dto.FactureRequest;
import com.example.FactureService.dto.FactureResponse;
import com.example.FactureService.model.Facture;
import com.example.FactureService.repository.FactureRepository;
import com.example.FactureService.service.exception.FactureNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FactureService {

    private final FactureRepository factureRepository;

    public FactureResponse createFacture(FactureRequest factureRequest) {
        Facture facture = Facture.builder()
                .dateFacture(factureRequest.getDateFacture())
                .idFournisseur(factureRequest.getIdFournisseur()) // Correction ici
                .idClient(factureRequest.getIdClient()) // Correction ici
                .idCommande(factureRequest.getIdCommande()) // Correction ici
                .produitsAdditionnels(Optional.ofNullable(factureRequest.getProduitsAdditionnels()).orElse(new HashMap<>()))
                .etat(factureRequest.getEtat())
                .montantTotal(factureRequest.getMontantTotal())
                .produits(factureRequest.getProduits())
                .build();

        facture = factureRepository.save(facture);
        log.info("Facture {} has been saved.", facture.getIdFacture());
        return mapToFactureResponse(facture);
    }

    public FactureResponse updateFacture(String id, FactureRequest factureRequest) {
        Facture existingFacture = factureRepository.findById(id)
                .orElseThrow(() -> new FactureNotFoundException("Cannot find facture with id: " + id));

        existingFacture.setEtat(factureRequest.getEtat());
        existingFacture.setDateFacture(factureRequest.getDateFacture());
        existingFacture.setIdFournisseur(factureRequest.getIdFournisseur());
        existingFacture.setIdClient(factureRequest.getIdClient());
        existingFacture.setIdCommande(factureRequest.getIdCommande());
        existingFacture.setProduitsAdditionnels(factureRequest.getProduitsAdditionnels());
        existingFacture.setProduits(factureRequest.getProduits());

        return mapToFactureResponse(factureRepository.save(existingFacture));
    }

    public List<FactureResponse> getAllFactures() {
        return factureRepository.findAll()
                .stream()
                .map(this::mapToFactureResponse)
                .toList();
    }

    public FactureResponse getFactureByIdClient(String idClient) {
        Facture facture = factureRepository.findByIdClient(idClient)
                .orElseThrow(() -> new FactureNotFoundException("Cannot find facture with idClient: " + idClient));
        return mapToFactureResponse(facture);
    }

    public FactureResponse getFactureById(String idFacture) {
        Facture facture = factureRepository.findById(idFacture)
                .orElseThrow(() -> new FactureNotFoundException("Facture not found with id: " + idFacture));
        return mapToFactureResponse(facture);
    }

    public void deleteFacture(String id) {
        factureRepository.deleteById(id);
        log.info("Facture with id {} has been deleted.", id);
    }

    private FactureResponse mapToFactureResponse(Facture facture) {
        return FactureResponse.builder()
                .idFacture(facture.getIdFacture())
                .dateFacture(facture.getDateFacture())
                .idFournisseur(facture.getIdFournisseur()) // Correction ici
                .idClient(facture.getIdClient()) // Correction ici
                .idCommande(facture.getIdCommande()) // Correction ici
                .produitsAdditionnels(facture.getProduitsAdditionnels())
                .etat(facture.getEtat())
                .montantTotal(facture.getMontantTotal())
                .produits(facture.getProduits())
                .build();
    }

    public String getFactureByIdCommande(String idCommande) {
        Facture facture = factureRepository.findByIdCommande(idCommande)
                .orElseThrow(() -> new FactureNotFoundException("Cannot find facture with idCommande: " + idCommande));
        return facture.getIdFacture();
    }
}
