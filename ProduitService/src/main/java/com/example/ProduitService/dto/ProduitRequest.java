package com.example.ProduitService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProduitRequest {



    private String nom;
    private Double prixUnitaire;
    private Integer seuilCritique;
    private Date dateAjout;
    private String IdStock;

}
