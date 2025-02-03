package com.example.StockService.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Stocks")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Stock {
    @Id
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
