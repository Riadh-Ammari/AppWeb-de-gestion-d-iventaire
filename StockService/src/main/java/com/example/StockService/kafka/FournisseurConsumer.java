package com.example.StockService.kafka;

import com.example.StockService.dto.StockRequest;
import com.example.StockService.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import primitives.FournisseurEvent;


@Service
public class FournisseurConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(FournisseurConsumer.class);
    private final StockService stockService;
    public FournisseurConsumer(StockService stockService) {
        this.stockService = stockService;
    }
    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(FournisseurEvent event){
        LOGGER.info("🎧 FournisseurEvent reçu: stockId = {}, fournisseurId = {}", event.getStockId(), event.getIdFournisseur());

        stockService.addFournisseurToStock(event.getStockId(), event.getIdFournisseur());
    }
}
