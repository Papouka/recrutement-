package com.beac.estage_backend.service;

import com.beac.estage_backend.dto.DashboardStatsDto;
import com.beac.estage_backend.dto.HrDashboardDto;
import com.beac.estage_backend.dto.RecentOfferDto;
import com.beac.estage_backend.model.OffreStage;
import com.beac.estage_backend.repository.CandidatureRepository;
import com.beac.estage_backend.repository.OffreStageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final CandidatureRepository candidatureRepository;
    private final OffreStageRepository offreStageRepository;

    public DashboardService(CandidatureRepository candidatureRepo, OffreStageRepository offreRepo) {
        this.candidatureRepository = candidatureRepo;
        this.offreStageRepository = offreRepo;
    }

    /**
     * Collecte et assemble toutes les données nécessaires pour le tableau de bord RH.
     */
    public HrDashboardDto getHrDashboardData() {

        // --- PARTIE 1 : Récupérer les 5 offres les plus récentes (logique corrigée) ---
        // On demande la première page (index 0), de 5 éléments, triée par 'dateCreation' en ordre décroissant.
        Pageable pageable = PageRequest.of(0, 5, Sort.by("dateCreation").descending());
        List<OffreStage> offresRecentes = offreStageRepository.findAll(pageable).getContent();

        // --- PARTIE 2 : Calculer les statistiques sur les candidatures ---
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotal(candidatureRepository.count());
        stats.setNouvelles(candidatureRepository.countByStatus("Reçue"));
        stats.setEnAnalyse(candidatureRepository.countByStatus("En analyse"));
        stats.setPreselectionnees(candidatureRepository.countByStatus("Présélectionnée"));

        // --- PARTIE 3 : Transformer les offres en DTOs ---
        List<RecentOfferDto> recentOfferDtos = offresRecentes.stream().map(offre -> {
            RecentOfferDto dto = new RecentOfferDto();
            dto.setId(offre.getId());
            dto.setTitre(offre.getTitre());
            dto.setStatut(offre.getStatut());
            dto.setNombreCandidats(candidatureRepository.countByOffreStageId(offre.getId()));
            return dto;
        }).collect(Collectors.toList());

        // --- PARTIE 4 : Assembler le DTO final pour la réponse ---
        HrDashboardDto dashboardData = new HrDashboardDto();
        dashboardData.setStats(stats);
        dashboardData.setRecentOffers(recentOfferDtos);

        return dashboardData;
    }
}