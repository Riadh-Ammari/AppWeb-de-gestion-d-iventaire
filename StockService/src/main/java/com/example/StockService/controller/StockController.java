package com.example.StockService.controller;

import com.example.StockService.dto.StockRequest;
import com.example.StockService.dto.StockResponse;
import com.example.StockService.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping
    public ResponseEntity<StockResponse> createStock(@RequestBody StockRequest stockRequest) {
        StockResponse stockResponse = stockService.createStock(stockRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(stockResponse);
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

    @GetMapping("/id/{id}") // Fixed PathVariable Name
    public ResponseEntity<StockResponse> getStockById(@PathVariable("id") String idStock) {
        StockResponse stockResponse = stockService.getStockById(idStock);
        return ResponseEntity.ok(stockResponse);
    }

    @GetMapping("/nom/{nom}")
    public ResponseEntity<StockResponse> getStockByName(@PathVariable String nom) {
        StockResponse stockResponse = stockService.getStockByName(nom);
        return ResponseEntity.ok(stockResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable String id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }
}
