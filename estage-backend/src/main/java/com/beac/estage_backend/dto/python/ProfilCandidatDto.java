package com.beac.estage_backend.dto.python;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // <-- IMPORTER
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // <-- AJOUTER CETTE LIGNE
public class ProfilCandidatDto {

    @JsonProperty("competences")
    private List<String> competences;

    // Pas besoin d'ajouter les autres champs (langue_detectee, etc.)
    // car l'annotation va simplement les ignorer.
}