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
public class ProduitRequest {


    private String photoUrl;
    private String nom;
    private Double prixUnitaire;
    private Integer seuilCritique;
    private Date dateAjout;
    private String idStock;

}
