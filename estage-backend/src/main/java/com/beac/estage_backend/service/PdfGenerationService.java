package com.beac.estage_backend.service;

import com.beac.estage_backend.model.Candidature;
import com.beac.estage_backend.model.OffreStage;
// Imports spécifiques à iTextPDF
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.time.format.DateTimeFormatter; // <-- NOUVEL IMPORT pour formater la date
import java.time.format.FormatStyle; // <-- NOUVEL IMPORT
import java.util.Locale; // <-- NOUVEL IMPORT

@Service
public class PdfGenerationService {

    /**
     * Génère un document PDF en mémoire à partir d'une liste de candidatures.
     * @param candidatures La liste des candidats à inclure dans le rapport.
     * @param offresMap Une map pour faire correspondre les IDs d'offres aux titres.
     * @return Un ByteArrayInputStream contenant les données du PDF.
     */
    public ByteArrayInputStream genererPdfRegroupe(Map<OffreStage, List<Candidature>> candidatsParOffre) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // --- Définition des polices (inchangé) ---
            Font fontTitrePrincipal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font fontTitreOffre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new BaseColor(0, 74, 153));
            Font fontEnteteTableau = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
            Font fontCellule = FontFactory.getFont(FontFactory.HELVETICA, 9);

            // --- NOUVEAU : Formateur de date ---
            // Pour afficher la date dans un format lisible
            DateTimeFormatter dateFormatter = DateTimeFormatter
                    .ofLocalizedDate(FormatStyle.LONG)
                    .withLocale(Locale.FRENCH);

            // --- Titre principal du document (inchangé) ---
            Paragraph titrePrincipal = new Paragraph("Liste des Candidats Retenus pour un Entretien", fontTitrePrincipal);
            titrePrincipal.setAlignment(Paragraph.ALIGN_CENTER);
            titrePrincipal.setSpacingAfter(25);
            document.add(titrePrincipal);

            for (Map.Entry<OffreStage, List<Candidature>> entry : candidatsParOffre.entrySet()) {
                OffreStage offre = entry.getKey();
                List<Candidature> candidatsPourCetteOffre = entry.getValue();

                // --- Titre de l'offre (inchangé) ---
                Paragraph titreOffrePara = new Paragraph(offre.getTitre(), fontTitreOffre);
                titreOffrePara.setSpacingBefore(20);
                titreOffrePara.setSpacingAfter(10);
                document.add(titreOffrePara);

                // --- CORRECTION : Mise à jour du tableau ---
                // On passe à 5 colonnes
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                // On ajuste la largeur des colonnes pour la nouvelle colonne
                table.setWidths(new float[]{2.5f, 2.5f, 4f, 2.5f, 2.5f});

                // En-tête du tableau mis à jour
                addSimpleTableHeaderWithDate(table, fontEnteteTableau);

                // Remplissage des données des candidats
                for (Candidature cand : candidatsPourCetteOffre) {
                    table.addCell(new Phrase(cand.getFirstName(), fontCellule));
                    table.addCell(new Phrase(cand.getLastName(), fontCellule));
                    table.addCell(new Phrase(cand.getEmail(), fontCellule));
                    table.addCell(new Phrase(cand.getPhone(), fontCellule));

                    // --- AJOUT : Formater et ajouter la date de soumission ---
                    String dateSoumission = (cand.getSubmissionDate() != null)
                            ? cand.getSubmissionDate().format(dateFormatter)
                            : "N/A";
                    table.addCell(new Phrase(dateSoumission, fontCellule));
                }

                document.add(table);
            }

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // --- CORRECTION : Nouvelle méthode pour l'en-tête avec la date ---
    private void addSimpleTableHeaderWithDate(PdfPTable table, Font font) {
        // On ajoute la nouvelle colonne
        String[] headers = {"Prénom", "Nom", "Email", "Téléphone", "Date de Soumission"};
        for (String headerTitle : headers) {
            PdfPCell headerCell = new PdfPCell();
            headerCell.setBackgroundColor(new BaseColor(0, 74, 153));
            headerCell.setPadding(6);
            headerCell.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
            headerCell.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
            headerCell.setPhrase(new Phrase(headerTitle, font));
            table.addCell(headerCell);
        }
    }
}
