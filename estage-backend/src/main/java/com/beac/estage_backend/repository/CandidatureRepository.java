package com.beac.estage_backend.repository;

import com.beac.estage_backend.model.Candidature;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatureRepository extends MongoRepository<Candidature, String>  {

    long countByStatus(String status);
    long countByOffreStageId(String offreId);
    List<Candidature> findByOffreStageId(String offreId);
    List<Candidature> findByParsedSkillsIn(List<String> skills);
    List<Candidature> findByStatus(String status);

}