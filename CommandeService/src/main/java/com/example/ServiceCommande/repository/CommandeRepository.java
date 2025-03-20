package com.example.ServiceCommande.repository;

import com.example.ServiceCommande.model.Commande;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommandeRepository extends MongoRepository<Commande, String> {

    @Query("{'idCommande': ?0}")
    Optional<Commande> findById(String idCommande);

    @Query("{'etat': ?0}")
    List<Commande> findByEtat(String etat);

    @Query("{'fournisseur': ?0}")
    List<Commande> findByFournisseur(String fournisseur);

    @Query("{'destination': ?0}")
    List<Commande> findByDestination(String destination);
}
