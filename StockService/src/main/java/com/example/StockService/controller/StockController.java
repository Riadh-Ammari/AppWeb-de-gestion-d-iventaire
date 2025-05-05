package com.example.StockService.controller;

import com.example.StockService.dto.ClientInfo;
import com.example.StockService.dto.FournisseurInfo;
import com.example.StockService.dto.StockRequest;
import com.example.StockService.dto.StockResponse;
import com.example.StockService.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping
    public ResponseEntity<StockResponse> createStock(@RequestBody StockRequest stockRequest) {
        StockResponse stockResponse = stockService.createStock(stockRequest);
        return ResponseEntity.status(201).body(stockResponse);
    }

    @PostMapping("/check")
    public ResponseEntity<Boolean> checkStock(@RequestBody Map<String, Integer> produits) {
        boolean isAvailable = stockService.isStockAvailable(produits);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/produit/{productId}/stock-id")
    public ResponseEntity<Map<String, String>> getStockIdByProductId(@PathVariable String productId) {
        String stockId = stockService.getStockIdByProductId(productId);
        Map<String, String> response = new HashMap<>();
        response.put("stockId", stockId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{stockId}/add-quantity/{productId}/{quantity}")
    public ResponseEntity<Void> addQuantiteToProduit(
            @PathVariable String stockId,
            @PathVariable String productId,
            @PathVariable int quantity) {
        stockService.addQuantiteToProduit(stockId, productId, quantity);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{stockId}/decrease-quantity/{productId}/{quantity}")
    public ResponseEntity<Void> decreaseQuantiteToProduit(
            @PathVariable String stockId,
            @PathVariable String productId,
            @PathVariable int quantity) {
        stockService.decreaseQuantiteToProduit(stockId, productId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{stockId}/remove-product/{productId}")
    public ResponseEntity<Void> removeProductFromStock(@PathVariable String stockId, @PathVariable String productId) {
        stockService.removeProductFromStock(stockId, productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{stockId}/add-product/{productId}")
    public ResponseEntity<Void> addProductToStock(@PathVariable String stockId, @PathVariable String productId) {
        stockService.addProductToStock(stockId, productId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{stockId}/add-client/{clientId}")
    public ResponseEntity<Void> addClientToStock(@PathVariable String stockId, @PathVariable String clientId) {
        stockService.addClientToStock(stockId, clientId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{stockId}/add-fournisseur/{fournisseurId}")
    public ResponseEntity<Void> addFournisseurToStock(@PathVariable String stockId, @PathVariable String fournisseurId) {
        stockService.addFournisseurToStock(stockId, fournisseurId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{stockId}/add-commande/{commandeId}")
    public ResponseEntity<Void> addCommandeToStock(@PathVariable String stockId, @PathVariable String commandeId) {
        stockService.addCommandeToStock(stockId, commandeId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockResponse> updateStock(@PathVariable String id, @RequestBody StockRequest stockRequest) {
        StockResponse stockResponse = stockService.updateStock(id, stockRequest);
        return ResponseEntity.ok(stockResponse);
    }

    @GetMapping
    public ResponseEntity<List<StockResponse>> getAllStocks() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<StockResponse> getStockById(@PathVariable String id) {
        return ResponseEntity.ok(stockService.getStockById(id));
    }

    @GetMapping("/nom/{nom}")
    public ResponseEntity<StockResponse> getStockByName(@PathVariable String nom) {
        return ResponseEntity.ok(stockService.getStockByName(nom));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable String id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{stockId}/clients")
    @ResponseStatus(HttpStatus.OK)
    public List<ClientInfo> getClientsByIdStock(@PathVariable String stockId) {
        return stockService.getClientsByIdStock(stockId);
    }

    @GetMapping("/{stockId}/fournisseurs")
    @ResponseStatus(HttpStatus.OK)
    public List<FournisseurInfo> getFournisseursByIdStock(@PathVariable String stockId) {
        return stockService.getFournisseursByIdStock(stockId);
    }
}