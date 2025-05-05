package com.example.StockService.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class StockRequest {

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
