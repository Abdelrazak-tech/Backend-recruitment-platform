package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.OffreEmploi;
import com.example.demo.entity.OffreEmploi.StatutOffre;
import com.example.demo.entity.RoleUtilisateur;
import com.example.demo.repository.OffreEmploiRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class OffreEmploiExpirationService {

    private static final Logger logger = LoggerFactory.getLogger(OffreEmploiExpirationService.class);

    private final OffreEmploiRepository offreEmploiRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public OffreEmploiExpirationService(OffreEmploiRepository offreEmploiRepository, JavaMailSender mailSender) {
        this.offreEmploiRepository = offreEmploiRepository;
        this.mailSender = mailSender;
    }

    // Vérifier les offres proches de l'expiration toutes les 24 heures
    @Scheduled(cron = "0 0 0 * * ?") // Exécuter tous les jours à minuit
    @Transactional
    public void verifierOffresExpiration() {
        logger.info("Début de la vérification des offres proches de l'expiration");

        List<OffreEmploi> offres = offreEmploiRepository.findAll();
        LocalDate today = LocalDate.now();
        logger.info("Nombre total d'offres dans la base de données : {}", offres.size());

        for (OffreEmploi offre : offres) {
            try {
                // Vérifier si l'offre est déjà expirée
                if (offre.getDateExpiration().isBefore(today) || offre.getDateExpiration().isEqual(today)) {
                    if (offre.getStatut() == StatutOffre.ACTIVE) {
                        logger.info("Offre ID {} est expirée (date d'expiration : {}). Mise à jour du statut à EXPIREE...", 
                                offre.getId(), offre.getDateExpiration());
                        offre.setStatut(StatutOffre.EXPIREE); // Marquer l'offre comme expirée
                        offreEmploiRepository.save(offre); // Sauvegarder les modifications
                        logger.info("Offre ID {} marquée comme EXPIREE avec succès", offre.getId());
                    } else {
                        logger.info("Offre ID {} est déjà marquée comme non active (statut : {}). Aucune action nécessaire.", 
                                offre.getId(), offre.getStatut());
                    }
                    continue;
                }

                // Vérifier si l'offre est proche de l'expiration (par exemple, 3 jours avant)
                LocalDate troisJoursAvantExpiration = offre.getDateExpiration().minusDays(3);
                if (today.equals(troisJoursAvantExpiration) && !offre.isNotificationEnvoyee()) {
                    logger.info("Offre ID {} est proche de l'expiration (dans 3 jours). Envoi d'une notification...", 
                            offre.getId());
                    envoyerNotificationExpiration(offre);
                    offre.setNotificationEnvoyee(true);
                    offreEmploiRepository.save(offre);
                    logger.info("Notification d'expiration envoyée pour l'offre ID {}", offre.getId());
                }
            } catch (Exception e) {
                logger.error("Erreur lors du traitement de l'offre ID {} : {}", offre.getId(), e.getMessage(), e);
            }
        }

        logger.info("Fin de la vérification des offres proches de l'expiration");
    }

    private void envoyerNotificationExpiration(OffreEmploi offre) {
        try {
            String email = offre.getUser().getEmail();
            String lienProlongation = "http://localhost:8080/api/dashboard/" + 
                (offre.getUser().getRole() == RoleUtilisateur.ADMINISTRATEUR ? "admin" : "recruteur") + 
                "/offres-emploi/" + offre.getId() + "/prolonger";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Votre offre d'emploi est sur le point d'expirer");
            String htmlContent = "<h2>Notification d'expiration d'offre</h2>"
                    + "<p>Votre offre intitulée <strong>" + offre.getTitre() + "</strong> va expirer le " + offre.getDateExpiration() + ".</p>"
                    + "<p>Souhaitez-vous prolonger cette offre ?</p>"
                    + "<p><a href=\"" + lienProlongation + "\">Prolonger l'offre</a></p>"
                    + "<p>Si vous ne prolongez pas, l'offre sera retirée de la plateforme à la date d'expiration mais restera dans notre base de données.</p>"
                    + "<p>À bientôt,<br>L'équipe de la plateforme de recrutement</p>";
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Email de notification d'expiration envoyé à : {}", email);
        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de la notification d'expiration pour l'offre ID {} : {}", 
                    offre.getId(), e.getMessage(), e);
        }
    }
}