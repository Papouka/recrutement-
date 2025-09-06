package com.beac.estage_backend.controller;

import com.beac.estage_backend.dto.CandidatureRequestDto;
import com.beac.estage_backend.model.Candidature;
import com.beac.estage_backend.model.OffreStage;
import com.beac.estage_backend.repository.CandidatureRepository;
import com.beac.estage_backend.repository.OffreStageRepository;
import com.beac.estage_backend.service.CandidatureService;
import com.beac.estage_backend.service.PdfGenerationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/candidatures")
public class CandidatureController {

    private final CandidatureService candidatureService;
    private final ObjectMapper objectMapper;
    private final PdfGenerationService pdfGenerationService;
    private final CandidatureRepository candidatureRepository;
    private final OffreStageRepository offreStageRepository;

    public CandidatureController(CandidatureService candidatureService, ObjectMapper objectMapper, PdfGenerationService pdfGenerationService,
                                 CandidatureRepository candidatureRepository,
                                 OffreStageRepository offreStageRepository) {
        this.candidatureService = candidatureService;
        this.objectMapper = objectMapper;
        this.pdfGenerationService = pdfGenerationService;
        this.candidatureRepository = candidatureRepository;
        this.offreStageRepository = offreStageRepository;
    }

    /**
     * Crée une nouvelle candidature à partir de données multipart.
     */
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createCandidature(
            @RequestParam("candidatureDto") String candidatureDtoJson,
            @RequestPart("cv") MultipartFile cv,
            @RequestPart("lettreMotivation") MultipartFile motivation,
            @RequestPart("diplome") MultipartFile diplome,
            @RequestPart("identite") MultipartFile identite
    ) {
        try {
            CandidatureRequestDto dto = objectMapper.readValue(candidatureDtoJson, CandidatureRequestDto.class);

            Candidature candidature = new Candidature();
            candidature.setFirstName(dto.getFirstName());
            candidature.setLastName(dto.getLastName());
            candidature.setEmail(dto.getEmail());
            candidature.setPhone(dto.getPhone());
            candidature.setNationality(dto.getNationality());
            candidature.setUniversity(dto.getUniversity());
            candidature.setFieldOfStudy(dto.getFieldOfStudy());
            candidature.setStudyLevel(dto.getStudyLevel());

            String offerId = dto.getOfferId();

            Candidature savedCandidature = candidatureService.saveCandidature(
                    candidature, cv, motivation, diplome, identite, offerId
            );

            return new ResponseEntity<>(savedCandidature, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Erreur lors du traitement de la candidature : " + e.getMessage());
        }
    }

    /**
     * Endpoint de recherche de candidats par compétences.
     * Exemple d'appel : GET /api/v1/candidatures/search-by-skills?skills=Java,SQL,React

     */
    @GetMapping("/search")
    public ResponseEntity<List<Candidature>> searchCandidatures(
            @RequestParam(required = false) List<String> requiredSkills,
            @RequestParam(required = false) List<String> desiredSkills,
            @RequestParam(required = false) String studyLevel,
            @RequestParam(required = false) String status
    ) {
        // On passe tous les critères au service
        List<Candidature> candidatures = candidatureService.searchByCriteria(
                requiredSkills, desiredSkills, studyLevel, status
        );
        return ResponseEntity.ok(candidatures);
    }



    /**
     * Récupère la liste de toutes les candidatures, avec options de filtrage.
     */
    @GetMapping
    public ResponseEntity<List<Candidature>> getAllCandidatures(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status
    ) {
        List<Candidature> candidatures = candidatureService.searchAndFilterCandidatures(search, status);
        return ResponseEntity.ok(candidatures);
    }

    /**
     * Récupère la liste des candidatures pour une offre de stage spécifique.
     */
    @GetMapping("/offre/{offreId}")
    public ResponseEntity<List<Candidature>> getCandidaturesByOffreId(@PathVariable String offreId) {
        List<Candidature> candidatures = candidatureService.findCandidaturesByOffreId(offreId);
        return ResponseEntity.ok(candidatures);
    }

    /**
     * Récupère les détails d'une seule candidature par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Candidature> getCandidatureById(@PathVariable String id) {
        Candidature candidature = candidatureService.findCandidatureById(id);
        return ResponseEntity.ok(candidature);
    }

    /**
     * Met à jour le statut d'une candidature.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
                                           @PathVariable String id,
                                           @RequestBody Map<String, String> body
    ) {
        String newStatus = body.get("status");
        if (newStatus == null || newStatus.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Le champ 'status' est manquant ou vide.");
        }
        Candidature updatedCandidature = candidatureService.updateCandidatureStatus(id, newStatus);
        // Retourne bien l'objet Candidature complet
        return ResponseEntity.ok(updatedCandidature);
    }


    /**
     * Endpoint pour générer et télécharger un rapport PDF des candidats en entretien.
     * Cette route doit être protégée et accessible uniquement par les RH.
     */
    @GetMapping(value = "/export/entretiens", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> exportEntretiensPdf() {

        // 1. Récupérer les candidats ayant le statut "Entretien"
        List<Candidature> listeEntretiens = candidatureRepository.findByStatus("Entretien");

        // Si la liste est vide, on peut renvoyer une réponse "No Content"
        if (listeEntretiens.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // 2. Récupérer les offres associées pour pouvoir afficher leurs titres
        // 1. Extraire les IDs des offres de stage
        List<String> offreIds = listeEntretiens.stream()
                .map(Candidature::getOffreStageId)
                .distinct()
                .collect(Collectors.toList());

        // 2. Récupérer les objets OffreStage correspondants
        Map<String, OffreStage> offresMap = offreStageRepository.findAllById(offreIds).stream()
                .collect(Collectors.toMap(OffreStage::getId, offre -> offre));

        // 3. Regrouper les candidats par objet OffreStage
        Map<OffreStage, List<Candidature>> candidatsParOffre = listeEntretiens.stream()
                .filter(c -> offresMap.containsKey(c.getOffreStageId())) // Sécurité pour éviter les erreurs
                .collect(Collectors.groupingBy(c -> offresMap.get(c.getOffreStageId())));



        // 4. Appeler le service PDF avec cette nouvelle structure de données
        ByteArrayInputStream bis = pdfGenerationService.genererPdfRegroupe(candidatsParOffre);

        HttpHeaders headers = new HttpHeaders();
        String filename = "liste_entretiens_par_offre.pdf";
        headers.add("Content-Disposition", "inline; filename=" + filename);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

}