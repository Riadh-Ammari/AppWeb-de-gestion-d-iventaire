package com.example.FactureService.repository;

import com.example.FactureService.model.Facture;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FactureRepository extends MongoRepository<Facture, String> {
    @Query("{'idClient': ?0}")
    Optional<Facture> findByIdClient(String idClient);
    @Query("{'idCommande': ?0}")
    Optional<Facture> findByIdCommande(String idCommande);
}
