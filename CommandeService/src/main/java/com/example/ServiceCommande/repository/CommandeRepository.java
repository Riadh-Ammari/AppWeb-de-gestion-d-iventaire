package com.example.ServiceCommande.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import primitives.Commande;

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

    // Find commands within a specific year and month
    @Query("{ 'dateCommande': { $gte: ?0, $lt: ?1 } }")
    List<Commande> findByYearAndMonth(String startDate, String endDate);

    // Find delivered commands within a specific year and month
    @Query("{ 'etat': 'livrée', 'dateCommande': { $gte: ?0, $lt: ?1 } }")
    List<Commande> findDeliveredByYearAndMonth(String startDate, String endDate);

    // Find pending delivery commands
    @Query("{ 'etat': 'En Cours' }")
    List<Commande> findPendingDelivery();
}