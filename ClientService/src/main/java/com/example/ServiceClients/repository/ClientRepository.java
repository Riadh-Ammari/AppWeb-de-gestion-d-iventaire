package com.example.ServiceClients.repository;

import com.example.ServiceClients.model.Client;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;


public interface ClientRepository extends MongoRepository<Client, String> {

    @Query("{'nom': ?0}")
    Optional<Client> findByNom(String nom);

    @Query("{'idClient': ?0}")
    Optional<Client> findById(String idClient);
}

