package com.beac.estage_backend.model;

import org.springframework.data.annotation.Id; // <-- Nouvel import
import org.springframework.data.mongodb.core.mapping.Document; // <-- Nouvel import
import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
@Document(collection = "offres_stage") // <-- Remplacer @Entity
public class OffreStage {

    @Id // L'annotation vient de org.springframework.data.annotation.Id
    private String id; // <-- Changer Long en String

    private String titre;
    private String description;
    private String statut;

    // Les @ElementCollection ne sont plus nécessaires, MongoDB gère les listes nativement.
    private List<String> requiredSkills = new ArrayList<>();
    private List<String> desiredSkills = new ArrayList<>();
    private List<String> requiredDiplomas = new ArrayList<>();
}