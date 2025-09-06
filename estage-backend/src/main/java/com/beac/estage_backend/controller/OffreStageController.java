package com.beac.estage_backend.controller;

import com.beac.estage_backend.dto.OfferDetailDto; // <-- Importer le DTO
import com.beac.estage_backend.model.OffreStage;
import com.beac.estage_backend.service.OffreStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/offres")
public class OffreStageController {

    private final OffreStageService offreStageService;

    @Autowired
    public OffreStageController(OffreStageService offreStageService) {
        this.offreStageService = offreStageService;
    }

    // --- READ (Toutes les offres pour la gestion interne) ---
    @GetMapping
    public List<OfferDetailDto> getAllOffres() {
        return offreStageService.findAllOffersWithCandidateCount();
    }
    // --- READ (Offres publiées pour la page d'accueil) ---
    @GetMapping("/publiees")
    public List<OffreStage> getPublishedOffres() {
        return offreStageService.findAllPublishedOffres();
    }

    // --- READ (Une seule offre par son ID, pour le formulaire d'édition) ---
    @GetMapping("/{id}")
    public ResponseEntity<OffreStage> getOffreById(@PathVariable String id) {
        OffreStage offre = offreStageService.findOffreById(id);
        return ResponseEntity.ok(offre);
    }

    // --- CREATE (Créer une nouvelle offre) ---
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OffreStage createOffre(@RequestBody OffreStage offre) {
        return offreStageService.createOffre(offre);
    }

    // --- UPDATE (Mettre à jour une offre existante) ---
    @PutMapping("/{id}")
    public ResponseEntity<OffreStage> updateOffre(@PathVariable String id, @RequestBody OffreStage offreDetails) {
        OffreStage offreMiseAJour = offreStageService.updateOffre(id, offreDetails);
        return ResponseEntity.ok(offreMiseAJour);
    }

    // --- DELETE (Supprimer une offre) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffre(@PathVariable String id) {
        offreStageService.deleteOffre(id);
        return ResponseEntity.noContent().build();
    }
}