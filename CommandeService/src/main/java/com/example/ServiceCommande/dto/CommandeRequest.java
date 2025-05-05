package com.example.ServiceCommande.dto;

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
public class CommandeRequest {

    private Date  dateCommande;
    private Double montantTotal;
    private String idClient;
    private String etat; // (en cours, livrée, annulée)
    private Map<String,Integer> produits;//ids des produits
    private String idFournisseur;
}
