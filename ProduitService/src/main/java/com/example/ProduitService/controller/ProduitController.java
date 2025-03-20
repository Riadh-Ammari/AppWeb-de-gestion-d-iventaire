
package com.example.ProduitService.controller;

import com.example.ProduitService.dto.ProduitRequest;
import com.example.ProduitService.dto.ProduitResponse;
import com.example.ProduitService.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produit")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProduitResponse> createProduit(@RequestBody ProduitRequest produitRequest) {
        ProduitResponse produitResponse = produitService.createProduit(produitRequest);
        return new ResponseEntity<>(produitResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProduitResponse> updateProduit(@PathVariable Long id, @RequestBody ProduitRequest produitRequest) {
        ProduitResponse produitResponse = produitService.updateProduit(id, produitRequest);
        return ResponseEntity.ok(produitResponse);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProduitResponse> getAllProduits() {

        return produitService.getAllProduits();
    }
    @GetMapping("/id/{idProduit}")
    public ResponseEntity<ProduitResponse> getProduitById(@PathVariable String idProduit) {
        ProduitResponse produitResponse = produitService.getProduitById(idProduit);
        return ResponseEntity.ok(produitResponse);
    }
    @PutMapping("/{id}/add-quantity/{quantity}")
    public ResponseEntity<ProduitResponse> addQuantiteToProduit(
            @PathVariable Long id, @PathVariable int quantity) {
        return ResponseEntity.ok(produitService.addQuantiteToProduit(id, quantity));
    }

    @PutMapping("/{id}/decrease-quantity/{quantity}")
    public ResponseEntity<ProduitResponse> decreaseQuantiteToProduit(
            @PathVariable Long id, @PathVariable int quantity) {
        return ResponseEntity.ok(produitService.decreaseQuantiteToProduit(id, quantity));
    }



    @GetMapping("/{nom}")
    public ResponseEntity<ProduitResponse> getProduitByName(@PathVariable String nom) {
        ProduitResponse produitResponse = produitService.getProduitByName(nom);
        return ResponseEntity.ok(produitResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable String id) {
        produitService.deleteProduit(id);
        return ResponseEntity.noContent().build();
    }
}
