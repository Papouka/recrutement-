package com.beac.estage_backend.repository;

import com.beac.estage_backend.model.OffreStage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query; // <-- Nouvel import
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OffreStageRepository extends MongoRepository<OffreStage, String> {

    /**
     * Trouve toutes les offres ayant un certain statut et les trie par
     * date de création, de la plus récente à la plus ancienne.
     * On utilise @Query pour être explicite.
     */
    @Query(value = "{ 'statut': ?0 }", sort = "{ 'dateCreation': -1 }")
    List<OffreStage> findByStatutOrderByDateCreationDesc(String statut);

    /**
     * Trouve les 5 offres les plus récentes en se basant sur leur date de création.
     * On utilise @Query pour être explicite et éviter les problèmes d'interprétation.
     */
    // --- CORRECTION DÉFINITIVE ---
    @Query(value = "{}", sort = "{ 'dateCreation': -1 }")
    List<OffreStage> findTop5ByOrderByDateCreationDesc();
}