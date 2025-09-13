package com.beac.estage_backend.config;

import com.beac.estage_backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UtilisateurRepository utilisateurRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // 1. On cherche notre utilisateur dans la base de données MongoDB
            com.beac.estage_backend.model.Utilisateur user = utilisateurRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));

            // --- C'EST LA CORRECTION DÉFINITIVE ---

            // 2. On vérifie que le rôle n'est pas vide dans la base de données
            if (user.getRole() == null || user.getRole().isBlank()) {
                // Si l'utilisateur n'a pas de rôle, il n'a aucune autorisation.
                throw new UsernameNotFoundException("L'utilisateur n'a aucun rôle défini.");
            }

            // 3. On crée une "autorité" à partir du rôle (String) stocké.
            //    Exemple: "ROLE_RH" devient un objet SimpleGrantedAuthority("ROLE_RH").
            GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());

            // 4. On retourne un objet UserDetails que Spring Security comprend,
            //    en lui passant la liste contenant cette autorité.
            return new User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(authority) // On passe la liste avec le rôle
            );
            // --- FIN DE LA CORRECTION ---
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}