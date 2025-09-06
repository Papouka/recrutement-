// src/main/java/com/beac/estage/dto/LoginResponseDto.java
package com.beac.estage_backend.dto;
import lombok.Data;
@Data
public class LoginResponseDto {
    private String token;
    private String firstName;
    private String lastName;
    private String role;
    // On pourrait ajouter l'ID de l'utilisateur, son rôle, etc.
}