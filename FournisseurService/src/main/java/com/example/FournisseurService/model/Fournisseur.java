package com.example.FournisseurService.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "Fournisseurs")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Fournisseur {

    @Id
    private String idFournisseur;
    private String nom;
    private String contact;
    private String adresse;
    private List<String> idCircuit;

}
