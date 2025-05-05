package com.example.FournisseurService.service;

import com.example.FournisseurService.dto.FournisseurInfo;
import com.example.FournisseurService.dto.FournisseurRequest;
import com.example.FournisseurService.dto.FournisseurResponse;
import com.example.FournisseurService.kafka.FournisseurProducer;
import com.example.FournisseurService.model.Fournisseur;
import com.example.FournisseurService.repository.FournisseurRepository;
import com.example.FournisseurService.service.exception.FournisseurNotFoundException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import primitives.FournisseurEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;
    private final FournisseurProducer fournisseurProducer;

    public FournisseurResponse createFournisseur(FournisseurRequest fournisseurRequest) {
        Fournisseur fournisseur = Fournisseur.builder()
                .nom(fournisseurRequest.getNom())
                .contact(fournisseurRequest.getContact())
                .adresse(fournisseurRequest.getAdresse())
                .deliveryAddresses(Optional.ofNullable(fournisseurRequest.getDeliveryAddresses()).orElse(new ArrayList<>()))
                .availability(Optional.ofNullable(fournisseurRequest.getAvailability()).orElse("libre"))
                .build();

        try {
            fournisseur = fournisseurRepository.save(fournisseur);
            log.info("Fournisseur {} ajouté avec succès", fournisseur.getIdFournisseur());
        } catch (Exception e) {
            log.error("Error saving fournisseur: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save fournisseur: " + e.getMessage());
        }

        FournisseurEvent event = new FournisseurEvent();
        event.setIdFournisseur(fournisseur.getIdFournisseur());
        event.setStockId(fournisseurRequest.getStockId());
        fournisseurProducer.envoyerMessage(event);

        return mapToFournisseurResponse(fournisseur);
    }

    public FournisseurResponse updateFournisseur(String id, FournisseurRequest fournisseurRequest) {
        Fournisseur existingFournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new FournisseurNotFoundException("Cannot find fournisseur with id: " + id));

        existingFournisseur.setNom(fournisseurRequest.getNom());
        existingFournisseur.setContact(fournisseurRequest.getContact());
        existingFournisseur.setAdresse(fournisseurRequest.getAdresse());
        existingFournisseur.setDeliveryAddresses(Optional.ofNullable(fournisseurRequest.getDeliveryAddresses())
                .orElse(existingFournisseur.getDeliveryAddresses()));
        existingFournisseur.setAvailability(Optional.ofNullable(fournisseurRequest.getAvailability())
                .orElse(existingFournisseur.getAvailability()));

        try {
            Fournisseur updatedFournisseur = fournisseurRepository.save(existingFournisseur);
            return mapToFournisseurResponse(updatedFournisseur);
        } catch (Exception e) {
            log.error("Error updating fournisseur with id: {}. {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update fournisseur: " + e.getMessage());
        }
    }

    public void updateFournisseurAvailability(String id, String availability, List<String> deliveryAddresses) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new FournisseurNotFoundException("Cannot find fournisseur with id: " + id));

        fournisseur.setAvailability(availability);
        fournisseur.setDeliveryAddresses(Optional.ofNullable(deliveryAddresses).orElse(new ArrayList<>()));

        try {
            fournisseurRepository.save(fournisseur);
            log.info("Fournisseur {} availability updated to {} with delivery addresses {}", id, availability, deliveryAddresses);
        } catch (Exception e) {
            log.error("Error updating fournisseur availability for id: {}. {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update fournisseur availability: " + e.getMessage());
        }
    }

    public List<FournisseurResponse> getAllFournisseurs() {
        try {
            Iterable<Fournisseur> fournisseursIterable = fournisseurRepository.findAll();
            List<Fournisseur> fournisseurs = StreamSupport.stream(fournisseursIterable.spliterator(), false)
                    .collect(Collectors.toList());

            return fournisseurs.stream()
                    .map(fournisseur -> {
                        try {
                            return mapToFournisseurResponse(fournisseur);
                        } catch (Exception e) {
                            log.error("Error mapping fournisseur with id: {}. Skipping document.",
                                    fournisseur.getIdFournisseur(), e);
                            return null;
                        }
                    })
                    .filter(fournisseurResponse -> fournisseurResponse != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching all fournisseurs: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch fournisseurs: " + e.getMessage());
        }
    }

    public FournisseurResponse getFournisseurById(String idFournisseur) {
        try {
            Fournisseur fournisseur = fournisseurRepository.findById(idFournisseur)
                    .orElseThrow(() -> new FournisseurNotFoundException("Fournisseur not found with id: " + idFournisseur));
            return mapToFournisseurResponse(fournisseur);
        } catch (Exception e) {
            log.error("Error fetching fournisseur with id: {}. {}", idFournisseur, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch fournisseur: " + e.getMessage());
        }
    }

    public FournisseurResponse getFournisseurByNom(String nom) {
        try {
            Fournisseur fournisseur = fournisseurRepository.findByNom(nom)
                    .orElseThrow(() -> new FournisseurNotFoundException("Cannot find fournisseur with name: " + nom));
            return mapToFournisseurResponse(fournisseur);
        } catch (Exception e) {
            log.error("Error fetching fournisseur with nom: {}. {}", nom, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch fournisseur: " + e.getMessage());
        }
    }

    public void deleteFournisseur(String id) {
        try {
            fournisseurRepository.deleteById(id);
            log.info("Fournisseur with id {} has been deleted.", id);
        } catch (Exception e) {
            log.error("Error deleting fournisseur with id: {}. {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete fournisseur: " + e.getMessage());
        }
    }

    public List<FournisseurInfo> getFournisseursByIdStock(List<String> fournisseurIds) {
        if (fournisseurIds == null || fournisseurIds.isEmpty()) {
            log.info("No fournisseur IDs provided for stock");
            return new ArrayList<>();
        }

        try {
            Iterable<Fournisseur> fournisseursIterable = fournisseurRepository.findAllById(fournisseurIds);
            List<Fournisseur> fournisseurs = StreamSupport.stream(fournisseursIterable.spliterator(), false)
                    .collect(Collectors.toList());

            return fournisseurs.stream()
                    .map(fournisseur -> FournisseurInfo.builder()
                            .idFournisseur(fournisseur.getIdFournisseur())
                            .nom(fournisseur.getNom())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching fournisseurs for stock IDs: {}. {}", fournisseurIds, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch fournisseurs for stock: " + e.getMessage());
        }
    }

    private FournisseurResponse mapToFournisseurResponse(Fournisseur fournisseur) {
        return FournisseurResponse.builder()
                .idFournisseur(fournisseur.getIdFournisseur())
                .nom(fournisseur.getNom())
                .contact(fournisseur.getContact())
                .adresse(fournisseur.getAdresse())
                .deliveryAddresses(fournisseur.getDeliveryAddresses())
                .availability(fournisseur.getAvailability())
                .build();
    }
}