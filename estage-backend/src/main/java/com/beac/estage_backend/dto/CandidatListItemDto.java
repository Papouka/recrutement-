package com.beac.estage_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Un constructeur avec tous les arguments sera pratique
public class CandidatListItemDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private Double matchingScore;
}