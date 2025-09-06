package com.beac.estage_backend.dto.python;

import java.util.List;

// Le record qui contient les détails du score
public record MatchingDetailsDto(
        double score_semantique, // Nom corrigé depuis votre code Python
        double score_mots_cles,  // Nom corrigé depuis votre code Python
        List<String> feedback_mots_cles
) {}