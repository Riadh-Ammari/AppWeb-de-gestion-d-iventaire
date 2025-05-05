package com.example.ServiceClient.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientLocation {
    private String adresse;
    private Double latitude;
    private Double longitude;
}
