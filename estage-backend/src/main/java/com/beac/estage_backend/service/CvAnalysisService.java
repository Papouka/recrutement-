package com.beac.estage_backend.service;

// --- DÉBUT DES IMPORTS À AJOUTER ---
import com.beac.estage_backend.dto.python.CvAnalysisResponseDto;
// --- FIN DES IMPORTS ---

import com.beac.estage_backend.model.OffreStage;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CvAnalysisService {

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${api.python.cv-analysis.url}")
    private String cvAnalysisApiUrl;

    public CvAnalysisService() {
        // Constructeur vide, car les dépendances sont initialisées directement
    }

    /**
     * Appelle l'API Python externe pour analyser un CV par rapport à une offre.
     * @param cvFile Le fichier du CV uploadé.
     * @param offre L'offre de stage pour laquelle le candidat postule.
     * @return Un DTO contenant les résultats de l'analyse.
     */
    public CvAnalysisResponseDto analyzeCv(MultipartFile cvFile, OffreStage offre) {
        try {
            // 1. Préparer les exigences de l'offre
            Map<String, Object> jobRequirements = new HashMap<>();

            // Ces méthodes fonctionneront une fois Lombok activé dans l'IDE
            List<String> requiredSkills = offre.getRequiredSkills();
            List<String> desiredSkills = offre.getDesiredSkills();

            jobRequirements.put("competences_requises", requiredSkills != null ? requiredSkills : new ArrayList<>());
            jobRequirements.put("competences_souhaitees", desiredSkills != null ? desiredSkills : new ArrayList<>());

            System.out.println("[DEBUG JAVA] Exigences envoyées à Python : " + jobRequirements.toString());

            // 2. Convertir les exigences en une chaîne JSON
            String jobReqJson = objectMapper.writeValueAsString(jobRequirements);

            // 3. Construire le corps de la requête multipart avec OkHttp
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("job_req_json", jobReqJson)
                    .addFormDataPart("cv_file",
                            Objects.requireNonNull(cvFile.getOriginalFilename()),
                            RequestBody.create(cvFile.getBytes(), MediaType.parse("application/pdf")))
                    .build();

            // 4. Construire la requête HTTP complète (URL, Headers, Body)
            Request request = new Request.Builder()
                    .url(cvAnalysisApiUrl)
                    .post(requestBody)
                    .build();

            // 5. Envoyer la requête et traiter la réponse
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "Pas de corps de réponse";
                    System.err.println("Erreur de l'API Python : " + errorBody);
                    throw new IOException("Réponse inattendue de l'API d'analyse : " + response);
                }

                // S'assurer que le corps de la réponse n'est pas null
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    throw new IOException("Le corps de la réponse de l'API d'analyse est vide.");
                }

                String responseBodyString = responseBody.string();
                System.out.println("[DEBUG JAVA] Réponse JSON de Python : " + responseBodyString);

                // 6. Désérialiser la réponse JSON en notre DTO Java
                return objectMapper.readValue(responseBodyString, CvAnalysisResponseDto.class);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'appel au service d'analyse de CV externe.", e);
        }
    }
}