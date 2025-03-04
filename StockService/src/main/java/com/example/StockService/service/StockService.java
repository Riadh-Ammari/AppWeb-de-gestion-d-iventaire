
package com.example.StockService.service;

import com.example.StockService.dto.StockRequest;
import com.example.StockService.dto.StockResponse;
import com.example.StockService.model.Stock;
import com.example.StockService.repository.StockRepository;
import com.example.StockService.service.exeception.StockNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockRepository stockRepository;

    public StockResponse createStock(StockRequest stockRequest) {
        Stock stock = Stock.builder()
                .idStock(stockRequest.getIdStock())
                .capacite(stockRequest.getCapacite())
                .quantiteTotalStocke(stockRequest.getQuantiteTotalStocke())
                .nom(stockRequest.getNom())
                .adresse(stockRequest.getAdresse())
                .etat(stockRequest.getEtat())
                .produits(Optional.ofNullable(stockRequest.getProduits()).orElse(new HashMap<>()))
                .historiqueCommandes(Optional.ofNullable(stockRequest.getHistoriqueCommandes()).orElse(new ArrayList<>()))
                .fournisseurs(Optional.ofNullable(stockRequest.getFournisseurs()).orElse(new ArrayList<>()))
                .clients(Optional.ofNullable(stockRequest.getClients()).orElse(new ArrayList<>()))
                .build();

        stock = stockRepository.save(stock);
        log.info("Stock {} has been saved.", stock.getIdStock());
        return mapToStockResponse(stock);
    }

    public StockResponse updateStock(String id, StockRequest stockRequest) {
        Stock existingStock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException("Cannot find stock with id: " + id));

        existingStock.setNom(stockRequest.getNom());
        existingStock.setEtat(stockRequest.getEtat());
        existingStock.setAdresse(stockRequest.getAdresse());
        existingStock.setCapacite(stockRequest.getCapacite());
        existingStock.setQuantiteTotalStocke(stockRequest.getQuantiteTotalStocke());

        Stock updatedStock = stockRepository.save(existingStock);
        return mapToStockResponse(updatedStock);
    }

    public List<StockResponse> getAllStocks() {
        return stockRepository.findAll()
                .stream()
                .map(this::mapToStockResponse)
                .toList();
    }

    public StockResponse getStockByName(String nom) {
        Stock stock = stockRepository.findByNom(nom)
                .orElseThrow(() -> new StockNotFoundException("Cannot find stock with name: " + nom));
        return mapToStockResponse(stock);
    }

    public StockResponse getStockById(String idStock) {
        Stock stock = stockRepository.findById(idStock)
                .orElseThrow(() -> new StockNotFoundException("Stock not found with id: " + idStock));
        return mapToStockResponse(stock);
    }

    public void deleteStock(String id) {
        stockRepository.deleteById(id);
        log.info("Stock with id {} has been deleted.", id);
    }

    public void addProductToStock(String stockId, String productId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found"));

        if (stock.getProduits() == null) {
            stock.setProduits(new HashMap<>());
        }

        stock.getProduits().put(productId, stock.getProduits().getOrDefault(productId, 0));
        stockRepository.save(stock);
    }

    public void addQuantiteToProduit(String stockId, String productId, int quantiteToAdd) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        // Ensure the product list is initialized
        Map<String, Integer> produits = stock.getProduits();
        if (produits == null) {
            produits = new HashMap<>();
            stock.setProduits(produits);
        }

        // Increase the quantity of the product
        produits.put(productId, produits.getOrDefault(productId, 0) + quantiteToAdd);
        stockRepository.save(stock);
    }


    public void decreaseQuantiteToProduit(String stockId, String productId, int quantiteToRemove) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        // Ensure the product list is initialized
        Map<String, Integer> produits = stock.getProduits();
        if (produits == null || !produits.containsKey(productId)) {
            throw new RuntimeException("Product not found in stock.");
        }

        int currentQuantity = produits.get(productId);
        if (currentQuantity < quantiteToRemove) {
            throw new RuntimeException("Insufficient stock quantity for product.");
        }

        // Decrease the quantity of the product
        produits.put(productId, currentQuantity - quantiteToRemove);
        stockRepository.save(stock);
    }



    public void removeProductFromStock(String stockId, String productId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found"));

        if (stock.getProduits() != null) {
            stock.getProduits().remove(productId);
        }

        stockRepository.save(stock);
    }

    private StockResponse mapToStockResponse(Stock stock) {
        return StockResponse.builder()
                .idStock(stock.getIdStock())
                .capacite(stock.getCapacite())
                .quantiteTotalStocke(stock.getQuantiteTotalStocke())
                .nom(stock.getNom())
                .adresse(stock.getAdresse())
                .etat(stock.getEtat())
                .produits(stock.getProduits())
                .historiqueCommandes(stock.getHistoriqueCommandes())
                .fournisseurs(stock.getFournisseurs())
                .clients(stock.getClients())
                .build();
    }
}
