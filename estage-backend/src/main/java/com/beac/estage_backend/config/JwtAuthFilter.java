package com.beac.estage_backend.config;

import com.beac.estage_backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre de sécurité qui s'exécute pour chaque requête.
 * Il intercepte les requêtes pour vérifier la présence et la validité d'un token JWT
 * dans l'en-tête 'Authorization'.
 */
@Component // Indique à Spring que c'est un composant qu'il doit gérer (un Bean)
@RequiredArgsConstructor // Lombok crée le constructeur pour les champs 'final'
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain // 'filterChain' représente la suite des filtres de sécurité
    ) throws ServletException, IOException {

        // 1. Récupérer l'en-tête 'Authorization' de la requête
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Vérifier si l'en-tête est présent et commence bien par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Si non, on ne fait rien et on passe la main au filtre suivant
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraire le token (tout ce qui se trouve après "Bearer ")
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt); // On demande au JwtService d'extraire l'email

        // 4. Vérifier si l'utilisateur n'est pas déjà authentifié dans le contexte de sécurité actuel
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // On charge les détails de l'utilisateur depuis la base de données
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 5. Valider le token (vérifier la signature, la date d'expiration, et le nom d'utilisateur)
            if (jwtService.validateToken(jwt, userDetails)) {
                // Si le token est valide, on crée un objet d'authentification pour Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // On ne met pas les credentials (mot de passe)
                        userDetails.getAuthorities() // On inclut les rôles (autorités)
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


                // --- LA CAMÉRA ---
                System.out.println("==========================================================");
                System.out.println("VALIDATION TOKEN - UTILISATEUR : " + userDetails.getUsername());
                System.out.println("AUTORITÉS CHARGÉES : " + userDetails.getAuthorities());
                System.out.println("==========================================================");

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // On passe la requête (potentiellement authentifiée) au filtre suivant
        filterChain.doFilter(request, response);
    }
}