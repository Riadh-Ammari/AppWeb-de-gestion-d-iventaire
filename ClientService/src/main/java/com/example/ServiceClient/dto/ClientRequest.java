package com.example.ServiceClient.dto;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientRequest {
    private String idClient;
    private String nom;
    private String contact;
    private String adresse;
    private List<String> commandes;
    private Map<String, Integer> produitsAdditionnelsAchetes;

    // Constructeurs, getters et setters
}
