package com.example.FournisseurService.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "Fournisseurs")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Fournisseur {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private String idFournisseur;
    private String nom;
    private String contact;
    private String adresse;
    private String availability;

    @JsonDeserialize(as = ArrayList.class)
    private List<String> deliveryAddresses = new ArrayList<>();

    public List<String> getDeliveryAddresses() {
        if (deliveryAddresses == null) {
            deliveryAddresses = new ArrayList<>();
        }
        return deliveryAddresses;
    }
}
