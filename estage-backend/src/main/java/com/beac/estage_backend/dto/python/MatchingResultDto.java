package com.beac.estage_backend.dto.python;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List; // <-- IMPORTER LA CLASSE List

@Data
public class MatchingResultDto {

    @JsonProperty("score_percent") // Bonne pratique pour mapper les noms python_case
    private double scorePercent;

    // --- CORRECTION ICI ---
    // On remplace l'objet unique par une Liste de String
    @JsonProperty("details")
    private List<String> details;

}