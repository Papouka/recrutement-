package com.beac.estage_backend.service;

import com.beac.estage_backend.dto.OfferDetailDto; // <-- IMPORT AJOUTÉ
import com.beac.estage_backend.exception.ResourceNotFoundException;
import com.beac.estage_backend.model.OffreStage;
import com.beac.estage_backend.repository.CandidatureRepository;
import com.beac.estage_backend.repository.OffreStageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OffreStageService {

    private final OffreStageRepository offreStageRepository;
    private final CandidatureRepository candidatureRepository;

    @Autowired
    public OffreStageService(OffreStageRepository offreStageRepository, CandidatureRepository candidatureRepository) {
        this.offreStageRepository = offreStageRepository;
        this.candidatureRepository = candidatureRepository;
    }

    /**
     * Récupère toutes les offres avec le nombre de candidats pour chacune.
     */
    public List<OfferDetailDto> findAllOffersWithCandidateCount() {
        List<OffreStage> offres = offreStageRepository.findAll();

        return offres.stream().map(offre -> {
            OfferDetailDto dto = new OfferDetailDto();
            dto.setId(offre.getId());
            dto.setTitre(offre.getTitre());
            dto.setStatut(offre.getStatut());
            dto.setDescription(offre.getDescription());

            // On passe l'ID (String) de l'offre au repository de candidature
            long count = candidatureRepository.countByOffreStageId(offre.getId());
            dto.setNombreCandidats(count);
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Récupère toutes les offres publiées, triées par date de création décroissante.
     */
    public List<OffreStage> findAllPublishedOffres() {
        // Assurez-vous que cette méthode existe dans votre OffreStageRepository
        return offreStageRepository.findByStatutOrderByDateCreationDesc("Publiée");
    }

    /**
     * Trouve une offre par son ID (String).
     */
    public OffreStage findOffreById(String id) {
        return offreStageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offre de stage non trouvée avec l'id : " + id));
    }

    /**
     * Crée une nouvelle offre.
     */
    public OffreStage createOffre(OffreStage offre) {
        // Pour MongoDB, il est préférable de ne pas toucher à l'ID pour la création.
        // S'il est null, MongoDB en créera un.
        return offreStageRepository.save(offre);
    }

    /**
     * Met à jour une offre existante.
     */
    public OffreStage updateOffre(String id, OffreStage offreDetails) {
        OffreStage offreExistante = findOffreById(id);

        offreExistante.setTitre(offreDetails.getTitre());
        offreExistante.setDescription(offreDetails.getDescription());
        offreExistante.setStatut(offreDetails.getStatut());
        offreExistante.setRequiredSkills(
                new ArrayList<>(offreDetails.getRequiredSkills() != null ? offreDetails.getRequiredSkills() : List.of())
        );
        offreExistante.setDesiredSkills(
                new ArrayList<>(offreDetails.getDesiredSkills() != null ? offreDetails.getDesiredSkills() : List.of())
        );
        offreExistante.setRequiredDiplomas(
                new ArrayList<>(offreDetails.getRequiredDiplomas() != null ? offreDetails.getRequiredDiplomas() : List.of())
        );

        return offreStageRepository.save(offreExistante);
    }

    /**
     * Supprime une offre par son ID (String).
     */
    public void deleteOffre(String id) {
        // On vérifie d'abord si l'offre existe. Si non, une exception sera levée.
        if (!offreStageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Impossible de supprimer, offre non trouvée avec l'id : " + id);
        }
        offreStageRepository.deleteById(id);
    }
}