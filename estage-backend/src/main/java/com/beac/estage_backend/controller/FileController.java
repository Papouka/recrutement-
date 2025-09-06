package com.beac.estage_backend.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/files") // La base de l'URL est /api/v1/files
public class FileController {

    // On définit le chemin vers notre dossier d'uploads
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    @GetMapping("/download/{filename:.+}") // Le chemin est /download/NOM_DU_FICHIER
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                throw new RuntimeException("Fichier non trouvé : " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Erreur de chemin de fichier : " + filename, ex);
        }
    }
}