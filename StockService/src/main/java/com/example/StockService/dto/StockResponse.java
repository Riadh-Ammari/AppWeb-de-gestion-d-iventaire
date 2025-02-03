package com.example.StockService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockResponse {
    public String IdStock;
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
