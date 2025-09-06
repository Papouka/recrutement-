package com.beac.estage_backend.service;

// --- DÉBUT DES IMPORTS À AJOUTER ---
import com.beac.estage_backend.dto.LoginRequestDto;
import com.beac.estage_backend.dto.LoginResponseDto;
import com.beac.estage_backend.dto.RegisterRequestDto;
import com.beac.estage_backend.exception.ResourceNotFoundException;
import com.beac.estage_backend.model.Utilisateur;
import com.beac.estage_backend.repository.UtilisateurRepository;
// --- FIN DES IMPORTS À AJOUTER ---

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public AuthService(
            UtilisateurRepository utilisateurRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Gère l'inscription d'un nouvel utilisateur CANDIDAT.
     */
    public Utilisateur register(RegisterRequestDto registerRequest) {
        if (utilisateurRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalStateException("Erreur : Un compte avec cet email existe déjà.");
        }

        Utilisateur nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setFirstName(registerRequest.getFirstName());
        nouvelUtilisateur.setLastName(registerRequest.getLastName());
        nouvelUtilisateur.setEmail(registerRequest.getEmail());
        nouvelUtilisateur.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        // Par défaut, un utilisateur qui s'inscrit via cette route est un candidat
        nouvelUtilisateur.setRole("ROLE_CANDIDAT");

        return utilisateurRepository.save(nouvelUtilisateur);
    }

    /**
     * Gère l'inscription d'un nouvel utilisateur RH.
     */
    public Utilisateur registerRh(RegisterRequestDto registerRequest) {
        if (utilisateurRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalStateException("Erreur : Un compte avec cet email existe déjà.");
        }
        Utilisateur nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setFirstName(registerRequest.getFirstName());
        nouvelUtilisateur.setLastName(registerRequest.getLastName());
        nouvelUtilisateur.setEmail(registerRequest.getEmail());
        nouvelUtilisateur.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        // On assigne spécifiquement le rôle RH
        nouvelUtilisateur.setRole("ROLE_RH");

        return utilisateurRepository.save(nouvelUtilisateur);
    }

    /**
     * Gère la connexion d'un utilisateur et génère un token JWT.
     */
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        // Demande à Spring Security de valider l'email et le mot de passe.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // On récupère notre objet Utilisateur complet pour avoir toutes les infos.
        Utilisateur utilisateur = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé après authentification."));

        // On génère un jeton JWT pour cet utilisateur.
        String token = jwtService.generateToken(userDetails);

        // On construit et retourne la réponse pour le frontend.
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setFirstName(utilisateur.getFirstName());
        response.setLastName(utilisateur.getLastName());
        response.setRole(utilisateur.getRole());

        return response;
    }
}