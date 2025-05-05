package com.example.FournisseurService.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FournisseurRequest {

    private String nom;
    private String contact;
    private String adresse;
    private List<String> deliveryAddresses = new ArrayList<>();
    private String stockId;
    private String availability;
}