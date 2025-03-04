package com.example.FournisseurService.repository;

import com.example.FournisseurService.model.Fournisseur;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface FournisseurRepository extends MongoRepository<Fournisseur,String> {
    @Query("{'nom': ?0}")
    Optional<Fournisseur> findByNom(String nom);
    @Query("{'idFournisseur': ?0}")
    Optional<Fournisseur> findById(String idFournisseur);
}
