package com.beac.estage_backend.controller;

// --- Imports existants ---
import com.beac.estage_backend.dto.LoginRequestDto;
import com.beac.estage_backend.dto.LoginResponseDto;
import com.beac.estage_backend.dto.RegisterRequestDto;
// --- IMPORT MANQUANT À AJOUTER ---
import com.beac.estage_backend.model.Utilisateur;
// --- FIN DE L'AJOUT ---
import com.beac.estage_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDto registerRequest) {
        try {
            // Cette ligne a besoin de connaître la classe 'Utilisateur'
            Utilisateur nouvelUtilisateur = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Utilisateur enregistré avec succès !");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/register-rh")
    public ResponseEntity<?> registerRh(@RequestBody RegisterRequestDto registerRequest) {
        try {
            // Cette ligne aussi
            Utilisateur nouvelUtilisateur = authService.registerRh(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Compte RH enregistré avec succès !");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDto loginRequest) {
        try {
            LoginResponseDto response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou mot de passe incorrect.");
        }
    }
}