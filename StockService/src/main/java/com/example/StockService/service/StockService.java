package com.example.StockService.service;

import com.example.StockService.dto.StockResponse;
import com.example.StockService.dto.StockRequest;
import com.example.StockService.model.Stock;
import com.example.StockService.repository.StockRepository;
import com.example.StockService.service.exeception.StockNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockRepository stockRepository;

    public StockResponse createStock(StockRequest stockRequest) {
        Stock stock = Stock.builder()
                .capacite(stockRequest.getCapacite())
                .quantiteStocke(stockRequest.getQuantiteStocke())
                .nom(stockRequest.getNom())
                .adresse(stockRequest.getAdresse())
                .etat(stockRequest.getEtat())
                .produits(stockRequest.getProduits())
                .historiqueCommandes(stockRequest.getHistoriqueCommandes())
                .fournisseurs(stockRequest.getFournisseurs())
                .clients(stockRequest.getClients())
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
        existingStock.setQuantiteStocke(stockRequest.getQuantiteStocke());

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

    private StockResponse mapToStockResponse(Stock stock) {
        return StockResponse.builder()
                .IdStock(stock.getIdStock())
                .capacite(stock.getCapacite())
                .quantiteStocke(stock.getQuantiteStocke())
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
