package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Candidat;
import com.example.demo.entity.Candidature;
import com.example.demo.entity.OffreEmploi;
import com.example.demo.entity.RoleUtilisateur;
import com.example.demo.entity.User;
import com.example.demo.repository.CandidatRepository;
import com.example.demo.repository.CandidatureRepository;
import com.example.demo.repository.OffreEmploiRepository;

@Service
public class CandidatureService {

    private static final Logger logger = LoggerFactory.getLogger(CandidatureService.class);

    private final CandidatureRepository candidatureRepository;
    private final CandidatRepository candidatRepository;
    private final OffreEmploiRepository offreEmploiRepository;
    private final NotificationService notificationService;

    public CandidatureService(CandidatureRepository candidatureRepository,
                              CandidatRepository candidatRepository,
                              OffreEmploiRepository offreEmploiRepository,
                              NotificationService notificationService) {
        this.candidatureRepository = candidatureRepository;
        this.candidatRepository = candidatRepository;
        this.offreEmploiRepository = offreEmploiRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Candidature postulerOffre(Long offreId, String emailCandidat) {
        // Récupérer le candidat connecté
        Candidat candidat = candidatRepository.findByEmail(emailCandidat)
                .orElseThrow(() -> {
                    logger.error("Candidat non trouvé : {}", emailCandidat);
                    return new IllegalArgumentException("Candidat non trouvé");
                });

        // Vérifier que l'utilisateur est bien un candidat
        if (candidat.getRole() != RoleUtilisateur.CANDIDAT) {
            logger.error("L'utilisateur {} n'est pas un candidat", emailCandidat);
            throw new IllegalArgumentException("Seul un candidat peut postuler à une offre");
        }

        // Récupérer l’offre d’emploi
        OffreEmploi offreEmploi = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> {
                    logger.error("Offre d'emploi non trouvée avec l'ID : {}", offreId);
                    return new IllegalArgumentException("Offre d'emploi non trouvée");
                });

        // Vérifier si l’offre est active
        if (offreEmploi.getStatut() != OffreEmploi.StatutOffre.ACTIVE) {
            logger.error("L'offre d'emploi ID {} n'est pas active", offreId);
            throw new IllegalArgumentException("Cette offre n'est plus active");
        }

        // Vérifier si le candidat a déjà postulé à cette offre
        if (candidatureRepository.existsByCandidatAndOffreEmploi(candidat, offreEmploi)) {
            logger.warn("Le candidat {} a déjà postulé à l'offre ID {}", emailCandidat, offreId);
            throw new IllegalArgumentException("Vous avez déjà postulé à cette offre");
        }

        // Vérifier que le candidat a un CV
        if (candidat.getCv() == null || candidat.getCv().length == 0) {
            logger.error("Le candidat {} n'a pas de CV dans son profil", emailCandidat);
            throw new IllegalArgumentException("Vous devez uploader un CV dans votre profil avant de postuler");
        }

        // Créer une nouvelle candidature et copier le CV
        Candidature candidature = new Candidature(candidat, offreEmploi, candidat.getCv());
        candidature = candidatureRepository.save(candidature);
        logger.info("Candidature créée avec succès : ID {}", candidature.getId());

        // Envoyer une notification in-app au recruteur/administrateur
        User destinataire = offreEmploi.getUser();
        String message = String.format("Nouvelle candidature de %s %s pour l’offre : %s", 
                candidat.getPrenom(), candidat.getNom(), offreEmploi.getTitre());
        String lien = "/offres-emploi/" + offreEmploi.getId() + "/candidatures";
        try {
            notificationService.creerNotification(destinataire, message, lien);
            logger.info("Notification in-app créée pour {} concernant la candidature ID {}", destinataire.getEmail(), candidature.getId());
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la notification pour {} : {}", destinataire.getEmail(), e.getMessage());
        }

        return candidature;
    }
}