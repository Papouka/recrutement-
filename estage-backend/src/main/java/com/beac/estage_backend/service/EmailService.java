package com.beac.estage_backend.service;

import com.beac.estage_backend.model.Candidature;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // Injecter l'adresse email et le nom de l'expéditeur depuis application.properties
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.from.name}")
    private String fromName;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envoie une invitation à un entretien à un candidat.
     * Utilise MimeMessage pour un contrôle total sur l'expéditeur.
     * @param candidature L'objet Candidature contenant les informations du candidat.
     */
    public void sendInterviewInvitation(Candidature candidature) {
        // MimeMessage est plus puissant que SimpleMailMessage
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            // L'assistant MimeMessageHelper facilite la construction de l'email
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // On force l'expéditeur avec l'email et le nom personnalisé
            helper.setFrom(new InternetAddress(fromEmail, fromName));
            helper.setTo(candidature.getEmail());
            helper.setSubject("Convocation à un entretien - Candidature de stage BEAC");

            // Corps de l'email
            String text = String.format(
                    "Bonjour %s %s,\n\n" +
                            "Suite à l'examen de votre candidature pour un stage au sein de la BEAC, nous avons le plaisir de vous informer que votre profil a retenu toute notre attention.\n\n" +
                            "Nous souhaiterions vous convier à un entretien afin d'échanger plus en détail sur votre parcours et vos motivations.\n\n" +
                            "Nous reviendrons vers vous très prochainement pour convenir d'une date et des modalités de cet entretien.\n\n" +
                            "Cordialement,\n\n" +
                            "L'équipe de recrutement\n" +
                            "e-Stage BEAC",
                    candidature.getFirstName(), candidature.getLastName()
            );
            helper.setText(text);

            // Envoi de l'email
            mailSender.send(mimeMessage);
            System.out.println("Email de convocation envoyé avec succès à " + candidature.getEmail());

        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("Erreur technique lors de la création ou l'envoi de l'email à " + candidature.getEmail());
            // Affiche l'erreur complète dans les logs pour le débogage
            e.printStackTrace();
        }
    }
}