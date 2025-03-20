package com.example.ServiceCommande.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "commandes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commande {
    @Id
    private String idCommande;
    private Date dateCommande;
    private Double montantTotal;
    private String destination;
    private String etat; // (en cours, livrée, annulée)
    private List<String> produits;//ids des produits
    private String fournisseur;
}
