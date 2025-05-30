package com.example.ServiceCommande.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyOrdersResponse {
    private Integer year;
    private Integer month;
    private List<CommandeResponse> orders;
}