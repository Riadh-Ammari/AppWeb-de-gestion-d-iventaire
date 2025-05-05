package com.example.FactureService.dto;

import lombok.*;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Map;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FactureResponse {

    private String idFacture; // ObjectId → String
    private Date dateFacture;
    private String idFournisseur;
    private String idClient;
    private String idCommande;
    private Map<String, Integer> produitsAdditionnels;
    private String etat;
    private Map<String,Integer> produits;
    private Double montantTotal;
}