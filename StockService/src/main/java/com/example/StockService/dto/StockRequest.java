package com.example.StockService.dto;

import lombok.*;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class StockRequest {
    private Integer capacite;
    private Integer quantiteStocke;
    private String adresse;
    private String nom;
    private String etat;
    private List<String> produits ;
    private List<String> historiqueCommandes;
    private List<String> fournisseurs ;
    private List<String> clients  ;
}
