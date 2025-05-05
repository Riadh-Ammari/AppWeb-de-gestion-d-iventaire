package com.example.StockService.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Document(collection = "Stocks")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Stock {
    @Id

    public String idStock;
    private Integer capacite;
    private Integer quantiteTotalStocke;
    private String adresse;
    private String nom;
    private String etat;
    private Map<String,Integer> produits;
    private List<String> historiqueCommandes;
    private List<String> fournisseurs ;
    private List<String> clients  ;

}
