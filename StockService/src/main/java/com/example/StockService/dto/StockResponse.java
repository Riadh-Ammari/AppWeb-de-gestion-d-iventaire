package com.example.StockService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockResponse {
    public String idStock;
    private Integer capacite;
    private Integer quantiteTotalStocke;
    private String adresse;
    private String nom;
    private String etat;
    private Map<String,Integer> produits ;
    private List<String> historiqueCommandes;
    private List<String> fournisseurs ;
    private List<String> clients  ;
}
