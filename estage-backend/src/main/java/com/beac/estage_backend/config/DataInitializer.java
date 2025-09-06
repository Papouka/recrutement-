package com.beac.estage_backend.config;

// --- DÉBUT DES IMPORTS À AJOUTER ---
import com.beac.estage_backend.model.OffreStage;
import com.beac.estage_backend.model.Utilisateur;
import com.beac.estage_backend.repository.OffreStageRepository;
import com.beac.estage_backend.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component; // <-- IMPORT ESSENTIEL
import java.util.List;
// --- FIN DES IMPORTS ---


@Component // <-- ANNOTATION ESSENTIELLE POUR QUE SPRING EXÉCUTE CE CODE
public class DataInitializer implements CommandLineRunner {

    private final OffreStageRepository offreStageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    // Le constructeur est correct, Spring injectera les dépendances
    public DataInitializer(
            OffreStageRepository offreStageRepository,
            UtilisateurRepository utilisateurRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.offreStageRepository = offreStageRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Vérification des données initiales...");

        // Créer un utilisateur RH de test s'il n'existe pas
        if (utilisateurRepository.findByEmail("rh.admin@beac.com").isEmpty()) {
            System.out.println("Création de l'utilisateur RH de test...");
            Utilisateur adminRh = new Utilisateur();
            adminRh.setFirstName("Admin");
            adminRh.setLastName("RH");
            adminRh.setEmail("rh.admin@beac.com");
            // On utilise le PasswordEncoder pour hacher le mot de passe
            adminRh.setPassword(passwordEncoder.encode("passwordAdmin123"));
            adminRh.setRole("ROLE_RH");
            utilisateurRepository.save(adminRh);
            System.out.println("Utilisateur RH créé avec succès.");
        }

        // Créer des offres de stage de test si la collection est vide
        if (offreStageRepository.count() == 0) {
            System.out.println("Création des offres de stage de test...");

            OffreStage offre1 = new OffreStage();
            offre1.setTitre("Stage en Audit Informatique");
            offre1.setDescription("Participez à l'audit des systèmes d'information.");
            offre1.setStatut("Publiée");
            offre1.setRequiredSkills(List.of("Audit SI", "Sécurité des réseaux", "COBIT"));
            offre1.setDesiredSkills(List.of("ISO 27001", "Python"));
            offre1.setRequiredDiplomas(List.of("Master en Informatique", "École d'ingénieur"));

            OffreStage offre2 = new OffreStage();
            offre2.setTitre("Stage en Analyse Financière");
            offre2.setDescription("Analysez les tendances du marché.");
            offre2.setStatut("Publiée");
            offre2.setRequiredSkills(List.of("Analyse financière", "Excel", "VBA"));
            offre2.setDesiredSkills(List.of("Power BI", "Modélisation"));
            offre2.setRequiredDiplomas(List.of("Master en Finance", "Master en Économie"));

            offreStageRepository.saveAll(List.of(offre1, offre2));

            System.out.println("Offres de stage créées avec succès.");
        }

        System.out.println("Initialisation des données terminée.");
    }
}