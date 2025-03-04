package com.example.FournisseurService.service;

import com.example.FournisseurService.dto.FournisseurRequest;
import com.example.FournisseurService.dto.FournisseurResponse;
import com.example.FournisseurService.model.Fournisseur;
import com.example.FournisseurService.repository.FournisseurRepository;
import com.example.FournisseurService.service.exception.FournisseurNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;

    public FournisseurResponse createFournisseur(FournisseurRequest fournisseurRequest) {
        Fournisseur fournisseur = Fournisseur.builder()
                .idFournisseur(fournisseurRequest.getIdFournisseur())
                .nom(fournisseurRequest.getNom())
                .contact(fournisseurRequest.getContact())
                .adresse(fournisseurRequest.getAdresse())
                .idCircuit(Optional.ofNullable(fournisseurRequest.getIdCircuit()).orElse(List.of()))
                .build();

        fournisseur = fournisseurRepository.save(fournisseur);
        return mapToFournisseurResponse(fournisseur);
    }

    public FournisseurResponse updateFournisseur(String id, FournisseurRequest fournisseurRequest) {
        Fournisseur existingFournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new FournisseurNotFoundException("Cannot find fournisseur with id: " + id));

        existingFournisseur.setNom(fournisseurRequest.getNom());
        existingFournisseur.setContact(fournisseurRequest.getContact());
        existingFournisseur.setAdresse(fournisseurRequest.getAdresse());
        existingFournisseur.setIdCircuit(Optional.ofNullable(fournisseurRequest.getIdCircuit()).orElse(existingFournisseur.getIdCircuit()));

        Fournisseur updatedFournisseur = fournisseurRepository.save(existingFournisseur);
        return mapToFournisseurResponse(updatedFournisseur);
    }

    public List<FournisseurResponse> getAllFournisseurs() {
        return fournisseurRepository.findAll()
                .stream()
                .map(this::mapToFournisseurResponse)
                .toList();
    }

    public FournisseurResponse getFournisseurById(String idFournisseur) {
        Fournisseur fournisseur = fournisseurRepository.findById(idFournisseur)
                .orElseThrow(() -> new FournisseurNotFoundException("Fournisseur not found with id: " + idFournisseur));
        return mapToFournisseurResponse(fournisseur);
    }

    public FournisseurResponse getFournisseurByNom(String nom) {
        Fournisseur fournisseur = fournisseurRepository.findByNom(nom)
                .orElseThrow(() -> new FournisseurNotFoundException("Cannot find fournisseur with name: " + nom));
        return mapToFournisseurResponse(fournisseur);
    }

    public void deleteFournisseur(String id) {
        fournisseurRepository.deleteById(id);
        log.info("Fournisseur with id {} has been deleted.", id);
    }

    private FournisseurResponse mapToFournisseurResponse(Fournisseur fournisseur) {
        return FournisseurResponse.builder()
                .idFournisseur(fournisseur.getIdFournisseur())
                .nom(fournisseur.getNom())
                .contact(fournisseur.getContact())
                .adresse(fournisseur.getAdresse())
                .idCircuit(fournisseur.getIdCircuit())
                .build();
    }
}
