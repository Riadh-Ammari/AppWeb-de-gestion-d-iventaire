package com.example.FactureService.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Document(collection = "Factures")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Facture {

    @Id
    private String idFacture; // ObjectId → String
    private Date dateFacture;
    private String idFournisseur;
    private String idClient;
    private String idCommande;
    private Map<String,Integer> produits;
    private Double montantTotal;
    private Map<String, Integer> produitsAdditionnels;
    private String etat;
}
