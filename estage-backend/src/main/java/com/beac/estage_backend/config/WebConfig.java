package com.beac.estage_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure le serveur de ressources statiques pour les fichiers uploadés.
     * Cette méthode est essentielle pour que le frontend puisse accéder aux CV
     * et lettres de motivation qui ont été sauvegardés sur le serveur.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Obtenir le chemin absolu vers le dossier 'uploads'
        Path uploadDirPath = Paths.get("uploads");
        String uploadPath = uploadDirPath.toFile().getAbsolutePath();

        System.out.println("Mappage du répertoire d'upload : " + uploadPath);

        // Crée le "pont" entre l'URL et le dossier physique :
        // Toute URL qui commence par "/uploads/"...
        registry.addResourceHandler("/uploads/**")
                // ...doit être mappée au dossier physique "uploads/" à la racine du projet.
                // Le préfixe "file:/" est crucial pour indiquer que c'est un chemin sur le disque.
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}
