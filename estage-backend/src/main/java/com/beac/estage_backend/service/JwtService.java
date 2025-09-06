package com.beac.estage_backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // IMPORTANT : Gardez cette clé secrète et longue.
    private static final String SECRET_KEY_STRING = "MaSuperCleSecretePourLeProjetEStageBeacQuiEstAssezLonguePourEtreSecuriseeEtDifficileADeviner";

    // Durée de validité du token (ici, 24 heures)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // --- DÉBUT DE LA SECTION À AJOUTER / VÉRIFIER ---

    /**
     * Extrait le nom d'utilisateur (l'email) du token JWT.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Valide un token en vérifiant s'il correspond à l'utilisateur et s'il n'a pas expiré.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Méthodes utilitaires pour la lecture du token

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // --- FIN DE LA SECTION À AJOUTER / VÉRIFIER ---


    // --- VOTRE CODE EXISTANT POUR LA GÉNÉRATION DE TOKEN ---

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // On pourrait ajouter les rôles dans le token ici si nécessaire
        // claims.put("roles", userDetails.getAuthorities());
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}