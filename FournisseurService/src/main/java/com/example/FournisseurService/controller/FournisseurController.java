package com.example.FournisseurService.controller;

import com.example.FournisseurService.dto.FournisseurInfo;
import com.example.FournisseurService.dto.FournisseurRequest;
import com.example.FournisseurService.dto.FournisseurResponse;
import com.example.FournisseurService.service.FournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fournisseur")
@RequiredArgsConstructor
public class FournisseurController {

    private final FournisseurService fournisseurService;

    @PostMapping
    public ResponseEntity<FournisseurResponse> createFournisseur(@RequestBody FournisseurRequest fournisseurRequest) {
        FournisseurResponse fournisseurResponse = fournisseurService.createFournisseur(fournisseurRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(fournisseurResponse);
    }

    @PostMapping("/by-stock")
    @ResponseStatus(HttpStatus.OK)
    public List<FournisseurInfo> getFournisseursByIdStock(@RequestBody List<String> fournisseurIds) {
        return fournisseurService.getFournisseursByIdStock(fournisseurIds);
    }

    @PutMapping("/{fournisseurId}")
    public ResponseEntity<FournisseurResponse> updateFournisseur(@PathVariable String fournisseurId, @RequestBody FournisseurRequest fournisseurRequest) {
        FournisseurResponse fournisseurResponse = fournisseurService.updateFournisseur(fournisseurId, fournisseurRequest);
        return ResponseEntity.ok(fournisseurResponse);
    }

    @PutMapping("/{fournisseurId}/availability")
    public ResponseEntity<Void> updateFournisseurAvailability(
            @PathVariable String fournisseurId,
            @RequestBody Map<String, Object> request) {
        String availability = (String) request.get("availability");
        @SuppressWarnings("unchecked")
        List<String> deliveryAddresses = (List<String>) request.get("deliveryAddresses");

        if (availability == null || (!availability.equals("libre") && !availability.equals("occupé"))) {
            return ResponseEntity.badRequest().build();
        }

        fournisseurService.updateFournisseurAvailability(fournisseurId, availability, deliveryAddresses);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FournisseurResponse> getAllFournisseurs() {
        return fournisseurService.getAllFournisseurs();
    }

    @GetMapping("/id/{idFournisseur}")
    public ResponseEntity<FournisseurResponse> getFournisseurById(@PathVariable String idFournisseur) {
        FournisseurResponse fournisseurResponse = fournisseurService.getFournisseurById(idFournisseur);
        return ResponseEntity.ok(fournisseurResponse);
    }

    @GetMapping("/nom/{nom}")
    public ResponseEntity<FournisseurResponse> getFournisseurByNom(@PathVariable String nom) {
        FournisseurResponse fournisseurResponse = fournisseurService.getFournisseurByNom(nom);
        return ResponseEntity.ok(fournisseurResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFournisseur(@PathVariable String id) {
        fournisseurService.deleteFournisseur(id);
        return ResponseEntity.noContent().build();
    }
}