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
    private List<String> idCommandes;
    private String idStock;

    // Constructeurs, getters et setters
}
