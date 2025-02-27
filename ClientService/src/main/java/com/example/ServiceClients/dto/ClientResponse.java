package com.example.ServiceClients.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponse {
    private String idClient;
    private String nom;
    private String contact;
    private String adresse;
    private List<String> commandes;
    private Map<String, Integer> produitsAdditionnelsAchetes;
}
