package com.example.ProduitService.repository;

import com.example.ProduitService.model.Produit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ProduitRepository extends MongoRepository<Produit,String > {
    @Query("{'nom': ?0}")

    Optional<Produit> findByNom(String nom);
    @Query("{'idProduit': ?0}")
    Optional<Produit> findByIdProduit(String idProduit);
}
