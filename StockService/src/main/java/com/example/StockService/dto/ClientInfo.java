package com.example.StockService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientInfo {
    private String idClient;
    private String nom;
}
