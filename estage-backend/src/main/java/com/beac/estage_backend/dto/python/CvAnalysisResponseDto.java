package com.beac.estage_backend.dto.python;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // <-- VÉRIFIER QUE CETTE LIGNE EST BIEN LÀ
public class CvAnalysisResponseDto {

    @JsonProperty("resultat_matching")
    private MatchingResultDto resultatMatching;

    @JsonProperty("profil_candidat")
    private ProfilCandidatDto profilCandidat;
}