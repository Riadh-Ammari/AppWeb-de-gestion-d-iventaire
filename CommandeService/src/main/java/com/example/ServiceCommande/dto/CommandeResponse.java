package com.example.ServiceCommande.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommandeResponse {
    private String idCommande;
    private Date dateCommande;
    private Double montantTotal;
    private String destination;
    private String etat; // (en cours, livrée, annulée)
    private List<String> produits;
    private String fournisseur;
}
