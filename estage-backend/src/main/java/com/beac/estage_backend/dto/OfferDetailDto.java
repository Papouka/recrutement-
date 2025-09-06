package com.beac.estage_backend.dto;

import lombok.Data;

@Data
public class OfferDetailDto {
    private String id;
    private String titre;
    private String statut;
    private String description; // <-- AJOUTER CE CHAMP S'IL MANQUE
    private long nombreCandidats;
    // Plus tard, vous pourrez ajouter d'autres champs comme une date de création
}