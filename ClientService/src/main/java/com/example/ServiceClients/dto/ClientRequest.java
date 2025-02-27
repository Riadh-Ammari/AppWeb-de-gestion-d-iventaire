package com.example.ServiceClients.dto;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientRequest {

    private String nom;
    private String contact;
    private String adresse;
    private List<String> commandes;
    private Map<String, Integer> produitsAdditionnelsAchetes;

    // Constructeurs, getters et setters
}
