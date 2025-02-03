package com.example.ProduitService.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
@Document(collection = "produits")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Produit {

    @Id
    public String idProduit;
    private String nom;
    private Double prixUnitaire;
    private Integer seuilCritique;
    private Date dateAjout;
    private String IdStock;

}

