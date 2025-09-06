// src/main/java/com/beac/estage/exception/ResourceNotFoundException.java
package com.beac.estage_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND) // Renvoie un statut 404 si cette exception est levée
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}