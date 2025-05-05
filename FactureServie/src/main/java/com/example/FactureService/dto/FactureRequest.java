package com.example.FactureService.dto;

import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FactureRequest {

    private Date dateFacture;
    private String idFournisseur;
    private String idClient;
    private String idCommande;
    private Map<String, Integer> produitsAdditionnels;
    private String etat;
    private Map<String,Integer> produits;//ids des produits
    private Double montantTotal;
}