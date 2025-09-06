package com.beac.estage_backend.service;


import com.beac.estage_backend.dto.python.CvAnalysisResponseDto;
import com.beac.estage_backend.dto.python.MatchingResultDto;
import com.beac.estage_backend.dto.python.ProfilCandidatDto;
// --- FIN DES IMPORTS ---

import com.beac.estage_backend.exception.ResourceNotFoundException;
import com.beac.estage_backend.model.Candidature;
import com.beac.estage_backend.model.OffreStage;
import com.beac.estage_backend.repository.CandidatureRepository;
import com.beac.estage_backend.repository.OffreStageRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class CandidatureService {

    private final CandidatureRepository candidatureRepository;
    private final FileStorageService fileStorageService;
    private final CvAnalysisService cvAnalysisService;
    private final OffreStageRepository offreStageRepository;
    private final EmailService emailService;
    private final MongoTemplate mongoTemplate;

    public CandidatureService(CandidatureRepository c, FileStorageService f, CvAnalysisService cv, OffreStageRepository o, EmailService e, MongoTemplate mt) {
        this.candidatureRepository = c;
        this.fileStorageService = f;
        this.cvAnalysisService = cv;
        this.offreStageRepository = o;
        this.emailService = e;
        this.mongoTemplate = mt;
    }


    public List<Candidature> findCandidaturesBySkills(List<String> skills) {
        if (skills == null || skills.isEmpty()) {
            return new ArrayList<>(); // Retourne une liste vide si aucune compétence n'est fournie
        }
        // On délègue simplement l'appel à notre nouvelle méthode de repository
        return candidatureRepository.findByParsedSkillsIn(skills);
    }

    public Candidature saveCandidature(
            Candidature candidature,
            MultipartFile cv,
            MultipartFile motivation,
            // Si vous réintégrez diplome et identite, il faudra les décommenter ici
             MultipartFile diplome,
             MultipartFile identite,
            String offerId
    ) {
        OffreStage offre = offreStageRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée avec l'ID: " + offerId));

        candidature.setOffreStageId(offre.getId());

        candidature.setCvPath(fileStorageService.storeFile(cv));
        candidature.setMotivationPath(fileStorageService.storeFile(motivation));
         candidature.setDiplomePath(fileStorageService.storeFile(diplome));
         candidature.setIdentitePath(fileStorageService.storeFile(identite));
        try {
            CvAnalysisResponseDto analysisResult = cvAnalysisService.analyzeCv(cv, offre);

            if (analysisResult != null) {
                MatchingResultDto matchingResult = analysisResult.getResultatMatching();
                ProfilCandidatDto profilCandidat = analysisResult.getProfilCandidat();

                if (matchingResult != null) {
                    candidature.setMatchingScore(matchingResult.getScorePercent());
                    if (matchingResult.getDetails() != null) {
                        candidature.setMatchingFeedback(String.join("\n", matchingResult.getDetails()));
                    }
                }
                if (profilCandidat != null && profilCandidat.getCompetences() != null) {
                    candidature.setParsedSkills(profilCandidat.getCompetences());
                }
            }
        } catch (Exception e) {
            System.err.println("AVERTISSEMENT : L'analyse du CV a échoué. Cause : " + e.getMessage());
            candidature.setMatchingScore(0.0);
            candidature.setMatchingFeedback("Analyse IA indisponible en raison d'une erreur technique.");
            candidature.setParsedSkills(new ArrayList<>());
        }

        return candidatureRepository.save(candidature);
    }


    public Candidature updateCandidatureStatus(String id, String newStatus) {
        Candidature candidature = findCandidatureById(id);
        candidature.setStatus(newStatus);
        Candidature updatedCandidature = candidatureRepository.save(candidature);

        if ("Entretien".equalsIgnoreCase(newStatus)) {
            emailService.sendInterviewInvitation(updatedCandidature);
        }
        return updatedCandidature;
    }

    public List<Candidature> searchByCriteria(List<String> requiredSkills, List<String> desiredSkills, String studyLevel, String status) {
        Query query = new Query();
        List<Criteria> allCriteria = new ArrayList<>();

        if (requiredSkills != null && !requiredSkills.isEmpty()) {
            // Pour les compétences requises, le candidat doit les avoir TOUTES
            allCriteria.add(Criteria.where("parsedSkills").all(requiredSkills));
        }
        if (desiredSkills != null && !desiredSkills.isEmpty()) {
            // Pour les compétences souhaitées, il suffit d'en avoir AU MOINS UNE
            allCriteria.add(Criteria.where("parsedSkills").in(desiredSkills));
        }
        if (studyLevel != null && !studyLevel.isEmpty()) {
            allCriteria.add(Criteria.where("studyLevel").is(studyLevel));
        }
        if (status != null && !status.isEmpty()) {
            allCriteria.add(Criteria.where("status").is(status));
        }

        if (!allCriteria.isEmpty()) {
            // On combine tous les critères avec un "ET" logique
            query.addCriteria(new Criteria().andOperator(allCriteria.toArray(new Criteria[0])));
        }

        // On exécute la requête dynamique
        return mongoTemplate.find(query, Candidature.class);
    }


    public List<Candidature> searchAndFilterCandidatures(String searchTerm, String status) {
        Query query = new Query();

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("Tous")) {
            query.addCriteria(Criteria.where("status").is(status));
        }

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("firstName").regex(searchTerm, "i"),
                    Criteria.where("lastName").regex(searchTerm, "i"),
                    Criteria.where("university").regex(searchTerm, "i"),
                    Criteria.where("fieldOfStudy").regex(searchTerm, "i")
            );
            query.addCriteria(searchCriteria);
        }

        return mongoTemplate.find(query, Candidature.class);
    }


    public List<Candidature> findCandidaturesByOffreId(String offreId) {
        return candidatureRepository.findByOffreStageId(offreId);
    }


    public Candidature findCandidatureById(String id) {
        return candidatureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature non trouvée avec l'id : " + id));
    }
}