package com.example.FournisseurService.dto;

import lombok.*;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FournisseurResponse {
    private String idFournisseur;
    private String nom;
    private String contact;
    private String adresse;
    private List<String> idCircuit;
}
