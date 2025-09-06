package com.beac.estage_backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed; // <-- Nouvel import pour l'unicité
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "utilisateurs") // On stocke ces objets dans la collection "utilisateurs"
public class Utilisateur {

    @Id
    private String id; // L'ID est maintenant un String (ObjectId de MongoDB)

    private String firstName;
    private String lastName;

    @Indexed(unique = true) // On dit à MongoDB de créer un index unique sur ce champ
    private String email;    // Cela garantit qu'il ne peut y avoir deux utilisateurs avec le même email

    private String password; // Le mot de passe haché
    private String role;
    // Plus tard, on pourra ajouter un champ pour les rôles :
    // private List<String> roles = new ArrayList<>();
}