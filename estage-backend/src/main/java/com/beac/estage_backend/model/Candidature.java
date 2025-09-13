package com.beac.estage_backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "candidatures")
public// Annotation pour MongoDB
 class Candidature {

    @Id
    private String id; // L'ID est un String

    // Informations Personnelles
    private String firstName;
    private String lastName;
    private String email; // La contrainte non-null est gérée au niveau service
    private String phone;
    private String nationality;

    // Parcours Académique
    private String university;
    private String fieldOfStudy;
    private String studyLevel;

    // Chemins des fichiers
    private String cvPath;
    private String motivationPath;
    private String diplomePath;
    private String identitePath;

    // Résultats IA
    private Double matchingScore = 0.0;
    private List<String> parsedSkills = new ArrayList<>(); // MongoDB gère les listes nativement
    private String matchingFeedback;

    // Métadonnées
    private LocalDate submissionDate = LocalDate.now();
    private String status = "Reçue";


    private String offreStageId; // On stocke l'ID, pas l'objet
}