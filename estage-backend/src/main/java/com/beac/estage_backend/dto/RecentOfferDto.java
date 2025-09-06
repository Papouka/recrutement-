// src/main/java/com/beac/estage_backend/dto/RecentOfferDto.java
package com.beac.estage_backend.dto;
import lombok.Data;
@Data
public class RecentOfferDto {
    private String id;
    private String titre;
    private String statut;
    private long nombreCandidats;
}