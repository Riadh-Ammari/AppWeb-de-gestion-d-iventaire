package com.example.ProduitService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProduitResponse {
    public String idProduit;
    private String nom;
    private Double prixUnitaire;
    private Integer seuilCritique;
    private Date dateAjout;
    private String idStock;
    private String photoUrl;


}