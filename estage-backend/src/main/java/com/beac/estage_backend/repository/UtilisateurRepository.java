package com.beac.estage_backend.repository;

// --- IMPORT MANQUANT À AJOUTER ---
import com.beac.estage_backend.model.Utilisateur;
// --- FIN DE L'AJOUT ---

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends MongoRepository<Utilisateur, String> {

    /**
     * Méthode magique de Spring Data MongoDB.
     * Comprend qu'il doit chercher un document par son champ "email".
     * Utilisée pour vérifier si un email existe déjà lors de l'inscription
     * et pour charger l'utilisateur lors de la connexion.
     */
    Optional<Utilisateur> findByEmail(String email);

}