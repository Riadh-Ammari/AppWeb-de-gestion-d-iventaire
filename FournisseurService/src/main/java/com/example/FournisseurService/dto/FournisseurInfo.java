package com.example.FournisseurService.dto;



import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FournisseurInfo {
    private String idFournisseur;
    private String nom;
}
