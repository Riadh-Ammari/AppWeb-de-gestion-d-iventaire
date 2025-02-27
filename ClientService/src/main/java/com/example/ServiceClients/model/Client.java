package com.example.ServiceClients.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    @Id
    private String idClient;
    private String nom;
    private String contact;
    private String adresse;
    private List<String> commandes;
    private Map<String, Integer> produitsAdditionnelsAchetes;
}
