package com.example.FactureService.controller;

import com.example.FactureService.dto.FactureRequest;
import com.example.FactureService.dto.FactureResponse;
import com.example.FactureService.service.FactureService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factures")
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;

    @PostMapping
    public ResponseEntity<FactureResponse> createFacture(@RequestBody FactureRequest factureRequest) {
        FactureResponse factureResponse = factureService.createFacture(factureRequest);
        return ResponseEntity.status(201).body(factureResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FactureResponse> updateFacture(@PathVariable String id, @RequestBody FactureRequest factureRequest) {
        return ResponseEntity.ok(factureService.updateFacture(id, factureRequest));

    }

    @GetMapping
    public ResponseEntity<List<FactureResponse>> getAllFactures() {
        return ResponseEntity.ok(factureService.getAllFactures());
    }

    @GetMapping("/id/{idFacture}")
    public ResponseEntity<FactureResponse> getFactureById(@PathVariable String idFacture) {
        return ResponseEntity.ok(factureService.getFactureById(idFacture));
    }
    @GetMapping("/Commande/{idCommande}")
    public ResponseEntity<String> getFactureByIdCommande(@PathVariable String idCommande) {
        return ResponseEntity.ok(factureService.getFactureByIdCommande(idCommande));
    }


    @GetMapping("/client/{idClient}")
    public ResponseEntity<FactureResponse> getFactureByIdClient(@PathVariable String idClient) {
        return ResponseEntity.ok(factureService.getFactureByIdClient(idClient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacture(@PathVariable String id) {
        factureService.deleteFacture(id);
        return ResponseEntity.noContent().build();
    }

}
