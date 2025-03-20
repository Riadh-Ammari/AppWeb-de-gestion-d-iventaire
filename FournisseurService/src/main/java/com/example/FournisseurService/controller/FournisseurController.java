package com.example.FournisseurService.controller;

import com.example.FournisseurService.dto.FournisseurRequest;
import com.example.FournisseurService.dto.FournisseurResponse;
import com.example.FournisseurService.service.FournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fournisseur")
@RequiredArgsConstructor
public class FournisseurController {

    private final FournisseurService fournisseurService;

    @PostMapping
    public ResponseEntity<FournisseurResponse> createFournisseur(@RequestBody FournisseurRequest fournisseurRequest) {
        FournisseurResponse fournisseurResponse = fournisseurService.createFournisseur(fournisseurRequest);
        return ResponseEntity.status(201).body(fournisseurResponse);
    }

    @PutMapping("/{fournisseurId}")
    public ResponseEntity<FournisseurResponse> updateFournisseur(@PathVariable String fournisseurId, @RequestBody FournisseurRequest fournisseurRequest) {
        FournisseurResponse fournisseurResponse = fournisseurService.updateFournisseur(fournisseurId, fournisseurRequest);
        return ResponseEntity.ok(fournisseurResponse);
    }

    @GetMapping
    public ResponseEntity<List<FournisseurResponse>> getAllFournisseurs() {
        List<FournisseurResponse> fournisseurResponses = fournisseurService.getAllFournisseurs();
        return ResponseEntity.ok(fournisseurResponses);
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
