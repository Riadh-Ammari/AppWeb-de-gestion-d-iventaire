package com.example.StockService.repository;

import com.example.StockService.model.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface StockRepository extends MongoRepository<Stock,String > {
    @Query("{'nom': ?0}")
    Optional<Stock> findByNom(String nom);
    @Query("{'IdStock': ?0}")
    Optional<Stock> findById(String IdStock);

}
