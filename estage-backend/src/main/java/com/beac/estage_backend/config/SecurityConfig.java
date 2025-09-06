package com.beac.estage_backend.config;

// --- Imports manquants ajoutés ---
import com.beac.estage_backend.repository.UtilisateurRepository;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
// --- Fin des imports ajoutés ---

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // --- Suppression de la classe dupliquée ici ---

    /**
     * Bean pour l'encodage des mots de passe.
     * Utilise l'algorithme BCrypt, qui est le standard de l'industrie.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean qui définit comment Spring Security doit charger les détails d'un utilisateur.
     * Il utilise notre UtilisateurRepository pour trouver un utilisateur par son email
     * et inclut son rôle.
     */
    @Bean
    public UserDetailsService userDetailsService(UtilisateurRepository utilisateurRepository) {
        return username -> {
            com.beac.estage_backend.model.Utilisateur user = utilisateurRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + username));

            GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());

            return new User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(authority)
            );
        };
    }

    /**
     * Bean qui expose l'AuthenticationManager de Spring Security.
     * Nécessaire pour que notre AuthService puisse gérer le processus de connexion.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean central qui définit la politique CORS (Cross-Origin Resource Sharing) pour toute l'application.
     * C'est ce qui autorise notre frontend React à communiquer avec notre backend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // N'oubliez pas de mettre à jour cette URL avec votre URL Ngrok/Cloudflare
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "https://fc53c518ecfe.ngrok-free.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * Le Bean principal qui configure toute la chaîne de filtres de sécurité de l'application.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Appliquer la configuration CORS définie dans le bean ci-dessus
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Désactiver la protection CSRF, car elle n'est pas nécessaire pour une API REST sans état
                .csrf(AbstractHttpConfigurer::disable)

                // Définir les règles d'autorisation pour chaque route
                .authorizeHttpRequests(auth -> auth
                        // Autoriser l'accès public à toutes les routes critiques
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/candidatures/**").permitAll()
                        .requestMatchers("/api/v1/offres/**").permitAll()
                        .requestMatchers("/api/v1/dashboard/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/v1/files/download/**").hasRole("RH")
                        .requestMatchers("/api/v1/candidatures/search-by-skills").hasRole("RH")
                        .requestMatchers("/api/v1/candidatures/export/entretiens").hasRole("RH")
                        .requestMatchers(HttpMethod.GET, "/api/v1/candidatures/**").hasRole("RH")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/candidatures/**").hasRole("RH")
                        .requestMatchers(HttpMethod.POST, "/api/v1/offres").hasRole("RH")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/offres/**").hasRole("RH")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/offres/**").hasRole("RH")

                        // Pour toutes les autres routes non définies ci-dessus, une authentification est requise
                        .anyRequest().authenticated()
                );

        // Note : Nous ajouterons le filtre JWT et les règles de rôles plus tard
        // .addFilterBefore(...)
        // .sessionManagement(...)

        return http.build();
    }
}