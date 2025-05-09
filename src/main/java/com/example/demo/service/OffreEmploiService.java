package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.OffreEmploiDTO;
import com.example.demo.entity.Candidat;
import com.example.demo.entity.OffreEmploi;
import com.example.demo.entity.OffreEmploi.StatutOffre;
import com.example.demo.entity.Recruteur;
import com.example.demo.entity.RoleUtilisateur;
import com.example.demo.entity.User;
import com.example.demo.repository.OffreEmploiRepository;
import com.example.demo.repository.UserRepository;

@Service
public class OffreEmploiService {

    private static final Logger logger = LoggerFactory.getLogger(OffreEmploiService.class);

    private final OffreEmploiRepository offreEmploiRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Autowired
    public OffreEmploiService(OffreEmploiRepository offreEmploiRepository, 
                              UserRepository userRepository,
                              EmailService emailService,
                              NotificationService notificationService) {
        this.offreEmploiRepository = offreEmploiRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    // Lister les offres d’un utilisateur spécifique
    @Transactional(readOnly = true)
    public List<OffreEmploi> listerOffresParUtilisateur(User user) {
        return offreEmploiRepository.findByUser(user);
    }

    // Lister les offres expirées d’un utilisateur spécifique
    @Transactional(readOnly = true)
    public List<OffreEmploi> listerOffresExpireesParUtilisateur(User user) {
        return offreEmploiRepository.findByUserAndStatut(user, StatutOffre.EXPIREE);
    }

    @Transactional
    public OffreEmploi creerOffreEmploi(OffreEmploiDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Création d'une offre d'emploi par l'utilisateur : {}", email);

        // Récupérer l'utilisateur connecté
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé : {}", email);
                    return new IllegalArgumentException("Utilisateur non trouvé");
                });

        // Vérifier que l'utilisateur est un administrateur ou un recruteur
        if (user.getRole() != RoleUtilisateur.ADMINISTRATEUR && user.getRole() != RoleUtilisateur.RECRUTEUR) {
            logger.error("L'utilisateur {} n'est pas autorisé à créer une offre", email);
            throw new IllegalArgumentException("Seul un administrateur ou un recruteur peut créer une offre d'emploi");
        }

        // Si l'utilisateur est un recruteur, utiliser sa raisonSociale comme nom de l'entreprise
        String entreprise = dto.getEntreprise();
        if (user.getRole() == RoleUtilisateur.RECRUTEUR) {
            if (!(user instanceof Recruteur)) {
                logger.error("L'utilisateur {} est marqué comme RECRUTEUR mais n'est pas une instance de Recruteur", email);
                throw new IllegalStateException("Utilisateur invalide : rôle RECRUTEUR mais pas une instance de Recruteur");
            }
            Recruteur recruteur = (Recruteur) user;
            if (recruteur.getRaisonSociale() == null || recruteur.getRaisonSociale().trim().isEmpty()) {
                logger.error("La raison sociale est manquante pour le recruteur : {}", email);
                throw new IllegalArgumentException("La raison sociale est obligatoire pour un recruteur");
            }
            entreprise = recruteur.getRaisonSociale();
            logger.info("Utilisateur est un recruteur. Le champ entreprise est défini à : {}", entreprise);
        }

        // Validation des champs
        if (dto.getTitre() == null || dto.getTitre().trim().isEmpty()) {
            logger.error("Le titre de l'offre est obligatoire");
            throw new IllegalArgumentException("Le titre de l'offre est obligatoire");
        }
        if (entreprise == null || entreprise.trim().isEmpty()) {
            logger.error("Le nom de l'entreprise est obligatoire");
            throw new IllegalArgumentException("Le nom de l'entreprise est obligatoire");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            logger.error("La description de l'offre est obligatoire");
            throw new IllegalArgumentException("La description de l'offre est obligatoire");
        }
        if (dto.getLocalisation() == null || dto.getLocalisation().trim().isEmpty()) {
            logger.error("La localisation de l'offre est obligatoire");
            throw new IllegalArgumentException("La localisation de l'offre est obligatoire");
        }
        if (dto.getTypeContrat() == null || dto.getTypeContrat().trim().isEmpty()) {
            logger.error("Le type de contrat est obligatoire");
            throw new IllegalArgumentException("Le type de contrat est obligatoire");
        }
        if (dto.getDateCreation() == null) {
            logger.error("La date de création est obligatoire");
            throw new IllegalArgumentException("La date de création est obligatoire");
        }
        if (dto.getDateExpiration() == null) {
            logger.error("La date d'expiration est obligatoire");
            throw new IllegalArgumentException("La date d'expiration est obligatoire");
        }
        if (dto.getDateExpiration().isBefore(dto.getDateCreation())) {
            logger.error("La date d'expiration ne peut pas être antérieure à la date de création");
            throw new IllegalArgumentException("La date d'expiration ne peut pas être antérieure à la date de création");
        }
        if (dto.getProfilRecherche() == null || dto.getProfilRecherche().isEmpty()) {
            logger.error("Les critères du profil recherché sont obligatoires");
            throw new IllegalArgumentException("Les critères du profil recherché sont obligatoires");
        }

        // Créer l'entité OffreEmploi
        OffreEmploi offreEmploi = new OffreEmploi();
        offreEmploi.setTitre(dto.getTitre());
        offreEmploi.setEntreprise(entreprise);
        offreEmploi.setDescription(dto.getDescription());
        offreEmploi.setLocalisation(dto.getLocalisation());
        offreEmploi.setTypeContrat(dto.getTypeContrat());
        offreEmploi.setDateCreation(dto.getDateCreation());
        offreEmploi.setDateExpiration(dto.getDateExpiration());
        offreEmploi.setDateDebut(dto.getDateDebut());
        offreEmploi.setSalaire(dto.getSalaire());
        offreEmploi.setStatut(StatutOffre.ACTIVE);
        offreEmploi.setMissions(dto.getMissions());
        offreEmploi.setProfilRecherche(dto.getProfilRecherche());
        offreEmploi.setAvantages(dto.getAvantages());
        offreEmploi.setContact(dto.getContact());
        offreEmploi.setInformationsEntreprise(dto.getInformationsEntreprise());
        offreEmploi.setProcessusRecrutement(dto.getProcessusRecrutement());
        offreEmploi.setUser(user);

        // Sauvegarder l'offre
        offreEmploi = offreEmploiRepository.save(offreEmploi);
        logger.info("Offre d'emploi créée avec succès : ID {}", offreEmploi.getId());

        // Récupérer tous les candidats inscrits (avec compte actif)
        List<User> candidats = userRepository.findByRoleAndCompteActif(RoleUtilisateur.CANDIDAT, true);
        logger.info("Nombre de candidats à notifier : {}", candidats.size());

        // Générer le lien vers l’offre
        String lienOffre = frontendUrl + "/offres/" + offreEmploi.getId();

        // Envoyer une notification par email et in-app à chaque candidat
        for (User userCandidat : candidats) {
            if (!(userCandidat instanceof Candidat)) {
                logger.warn("L'utilisateur {} est marqué comme CANDIDAT mais n'est pas une instance de Candidat", userCandidat.getEmail());
                continue;
            }
            Candidat candidat = (Candidat) userCandidat;

            // 1. Notification par email
            String subject = "Nouvelle offre d'emploi publiée : " + offreEmploi.getTitre();
            String emailBody = String.format(
                "Bonjour %s %s,\n\n" +
                "Une nouvelle offre d'emploi a été publiée sur la plateforme !\n\n" +
                "Titre : %s\n" +
                "Entreprise : %s\n" +
                "Localisation : %s\n" +
                "Type de contrat : %s\n" +
                "Description : %s\n\n" +
                "Consultez les détails de l'offre : %s\n\n" +
                "Cordialement,\n" +
                "L'équipe de la plateforme",
                candidat.getPrenom(), candidat.getNom(),
                offreEmploi.getTitre(), offreEmploi.getEntreprise(),
                offreEmploi.getLocalisation(), offreEmploi.getTypeContrat(),
                offreEmploi.getDescription(), lienOffre
            );
            try {
                emailService.sendEmail(candidat.getEmail(), subject, emailBody);
                logger.info("Notification par email envoyée au candidat : {}", candidat.getEmail());
            } catch (Exception e) {
                logger.error("Erreur lors de l'envoi de l'email au candidat {} : {}", candidat.getEmail(), e.getMessage());
            }

            // 2. Notification in-app
            String message = String.format("Nouvelle offre : %s chez %s", offreEmploi.getTitre(), offreEmploi.getEntreprise());
            try {
                notificationService.creerNotification(candidat, message, lienOffre);
                logger.info("Notification in-app créée pour le candidat : {}", candidat.getEmail());
            } catch (Exception e) {
                logger.error("Erreur lors de la création de la notification in-app pour le candidat {} : {}", candidat.getEmail(), e.getMessage());
            }
        }

        return offreEmploi;
    }

    @Transactional
    public OffreEmploi modifierOffreEmploi(Long offreId, OffreEmploiDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Modification de l'offre d'emploi ID : {} par l'utilisateur : {}", offreId, email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé : {}", email);
                    return new IllegalArgumentException("Utilisateur non trouvé");
                });

        if (user.getRole() != RoleUtilisateur.ADMINISTRATEUR && user.getRole() != RoleUtilisateur.RECRUTEUR) {
            logger.error("L'utilisateur {} n'est pas autorisé à modifier une offre", email);
            throw new IllegalArgumentException("Seul un administrateur ou un recruteur peut modifier une offre d'emploi");
        }

        OffreEmploi offreEmploi = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> {
                    logger.error("Offre d'emploi non trouvée avec l'ID : {}", offreId);
                    return new IllegalArgumentException("Offre d'emploi non trouvée");
                });

        if (user.getRole() != RoleUtilisateur.ADMINISTRATEUR && !offreEmploi.getUser().getId().equals(user.getId())) {
            logger.error("L'utilisateur {} n'a pas créé cette offre (ID {})", email, offreId);
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier cette offre");
        }

        String entreprise = dto.getEntreprise();
        if (user.getRole() == RoleUtilisateur.RECRUTEUR) {
            if (!(user instanceof Recruteur)) {
                logger.error("L'utilisateur {} est marqué comme RECRUTEUR mais n'est pas une instance de Recruteur", email);
                throw new IllegalStateException("Utilisateur invalide : rôle RECRUTEUR mais pas une instance de Recruteur");
            }
            Recruteur recruteur = (Recruteur) user;
            if (recruteur.getRaisonSociale() == null || recruteur.getRaisonSociale().trim().isEmpty()) {
                logger.error("La raison sociale est manquante pour le recruteur : {}", email);
                throw new IllegalArgumentException("La raison sociale est obligatoire pour un recruteur");
            }
            entreprise = recruteur.getRaisonSociale();
            logger.info("Utilisateur est un recruteur. Le champ entreprise est défini à : {}", entreprise);
        }

        if (dto.getTitre() == null || dto.getTitre().trim().isEmpty()) {
            logger.error("Le titre de l'offre est obligatoire");
            throw new IllegalArgumentException("Le titre de l'offre est obligatoire");
        }
        if (entreprise == null || entreprise.trim().isEmpty()) {
            logger.error("Le nom de l'entreprise est obligatoire");
            throw new IllegalArgumentException("Le nom de l'entreprise est obligatoire");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            logger.error("La description de l'offre est obligatoire");
            throw new IllegalArgumentException("La description de l'offre est obligatoire");
        }
        if (dto.getLocalisation() == null || dto.getLocalisation().trim().isEmpty()) {
            logger.error("La localisation de l'offre est obligatoire");
            throw new IllegalArgumentException("La localisation de l'offre est obligatoire");
        }
        if (dto.getTypeContrat() == null || dto.getTypeContrat().trim().isEmpty()) {
            logger.error("Le type de contrat est obligatoire");
            throw new IllegalArgumentException("Le type de contrat est obligatoire");
        }
        if (dto.getDateCreation() == null) {
            logger.error("La date de création est obligatoire");
            throw new IllegalArgumentException("La date de création est obligatoire");
        }
        if (dto.getDateExpiration() == null) {
            logger.error("La date d'expiration est obligatoire");
            throw new IllegalArgumentException("La date d'expiration est obligatoire");
        }
        if (dto.getDateExpiration().isBefore(dto.getDateCreation())) {
            logger.error("La date d'expiration ne peut pas être antérieure à la date de création");
            throw new IllegalArgumentException("La date d'expiration ne peut pas être antérieure à la date de création");
        }
        if (dto.getProfilRecherche() == null || dto.getProfilRecherche().isEmpty()) {
            logger.error("Les critères du profil recherché sont obligatoires");
            throw new IllegalArgumentException("Les critères du profil recherché sont obligatoires");
        }

        offreEmploi.setTitre(dto.getTitre());
        offreEmploi.setEntreprise(entreprise);
        offreEmploi.setDescription(dto.getDescription());
        offreEmploi.setLocalisation(dto.getLocalisation());
        offreEmploi.setTypeContrat(dto.getTypeContrat());
        offreEmploi.setDateCreation(dto.getDateCreation());
        offreEmploi.setDateExpiration(dto.getDateExpiration());
        offreEmploi.setDateDebut(dto.getDateDebut());
        offreEmploi.setSalaire(dto.getSalaire());
        offreEmploi.setMissions(dto.getMissions());
        offreEmploi.setProfilRecherche(dto.getProfilRecherche());
        offreEmploi.setAvantages(dto.getAvantages());
        offreEmploi.setContact(dto.getContact());
        offreEmploi.setInformationsEntreprise(dto.getInformationsEntreprise());
        offreEmploi.setProcessusRecrutement(dto.getProcessusRecrutement());

        // Mettre à jour le statut en fonction des nouvelles dates
        if (dto.getDateExpiration().isBefore(LocalDate.now())) {
            offreEmploi.setStatut(StatutOffre.EXPIREE);
        } else {
            offreEmploi.setStatut(StatutOffre.ACTIVE);
        }

        offreEmploi = offreEmploiRepository.save(offreEmploi);
        logger.info("Offre d'emploi modifiée avec succès : ID {}", offreEmploi.getId());
        return offreEmploi;
    }

    @Transactional
    public void supprimerOffreEmploi(Long offreId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Suppression de l'offre d'emploi ID : {} par l'utilisateur : {}", offreId, email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé : {}", email);
                    return new IllegalArgumentException("Utilisateur non trouvé");
                });

        if (user.getRole() != RoleUtilisateur.ADMINISTRATEUR && user.getRole() != RoleUtilisateur.RECRUTEUR) {
            logger.error("L'utilisateur {} n'est pas autorisé à supprimer une offre", email);
            throw new IllegalArgumentException("Seul un administrateur ou un recruteur peut supprimer une offre d'emploi");
        }

        OffreEmploi offreEmploi = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> {
                    logger.error("Offre d'emploi non trouvée avec l'ID : {}", offreId);
                    return new IllegalArgumentException("Offre d'emploi non trouvée");
                });

        if (user.getRole() != RoleUtilisateur.ADMINISTRATEUR && !offreEmploi.getUser().getId().equals(user.getId())) {
            logger.error("L'utilisateur {} n'a pas créé cette offre (ID {})", email, offreId);
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à supprimer cette offre");
        }

        offreEmploiRepository.delete(offreEmploi);
        logger.info("Offre d'emploi supprimée avec succès : ID {}", offreId);
    }

    @Transactional
    public OffreEmploi prolongerOffreEmploi(Long offreId, boolean prolonger) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Prolongation de l'offre d'emploi ID : {} par l'utilisateur : {}", offreId, email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé : {}", email);
                    return new IllegalArgumentException("Utilisateur non trouvé");
                });

        if (user.getRole() != RoleUtilisateur.ADMINISTRATEUR && user.getRole() != RoleUtilisateur.RECRUTEUR) {
            logger.error("L'utilisateur {} n'est pas autorisé à prolonger une offre", email);
            throw new IllegalArgumentException("Seul un administrateur ou un recruteur peut prolonger une offre d'emploi");
        }

        OffreEmploi offreEmploi = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> {
                    logger.error("Offre d'emploi non trouvée avec l'ID : {}", offreId);
                    return new IllegalArgumentException("Offre d'emploi non trouvée");
                });

        if (user.getRole() != RoleUtilisateur.ADMINISTRATEUR && !offreEmploi.getUser().getId().equals(user.getId())) {
            logger.error("L'utilisateur {} n'a pas créé cette offre (ID {})", email, offreId);
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à prolonger cette offre");
        }

        if (prolonger) {
            offreEmploi.setDateExpiration(offreEmploi.getDateExpiration().plusDays(30));
            offreEmploi.setNotificationEnvoyee(false);
            offreEmploi.setStatut(StatutOffre.ACTIVE);
            logger.info("Offre ID {} prolongée de 30 jours, nouvelle date d'expiration : {}", offreId, offreEmploi.getDateExpiration());
        } else {
            offreEmploiRepository.delete(offreEmploi);
            logger.info("Offre ID {} supprimée à la demande de l'utilisateur", offreId);
            return null;
        }

        return offreEmploiRepository.save(offreEmploi);
    }

    @Transactional
    public List<OffreEmploi> listerToutesLesOffres() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Liste de toutes les offres demandée par l'utilisateur : {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé : {}", email);
                    return new IllegalArgumentException("Utilisateur non trouvé");
                });

        if (user.getRole() != RoleUtilisateur.ADMINISTRATEUR) {
            logger.error("L'utilisateur {} n'est pas autorisé à lister toutes les offres", email);
            throw new IllegalArgumentException("Seul un administrateur peut lister toutes les offres");
        }

        List<OffreEmploi> offres = offreEmploiRepository.findByStatut(StatutOffre.ACTIVE);
        logger.info("Nombre total d'offres actives récupérées : {}", offres.size());
        return offres;
    }

    @Transactional
    public List<OffreEmploi> listerMesOffresActives() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Liste des offres demandée par l'utilisateur : {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé : {}", email);
                    return new IllegalArgumentException("Utilisateur non trouvé");
                });

        if (user.getRole() != RoleUtilisateur.RECRUTEUR) {
            logger.error("L'utilisateur {} n'est pas autorisé à lister ses offres", email);
            throw new IllegalArgumentException("Seul un recruteur peut lister ses propres offres");
        }

        List<OffreEmploi> offres = offreEmploiRepository.findByUserAndStatut(user, StatutOffre.ACTIVE);
        logger.info("Nombre d'offres actives récupérées pour l'utilisateur {} : {}", email, offres.size());
        return offres;
    }

    @Transactional
    public List<OffreEmploi> listerOffresExpirees() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Liste des offres expirées demandée par l'utilisateur : {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé : {}", email);
                    return new IllegalArgumentException("Utilisateur non trouvé");
                });

        if (user.getRole() != RoleUtilisateur.ADMINISTRATEUR) {
            logger.error("L'utilisateur {} n'est pas autorisé à lister les offres expirées", email);
            throw new IllegalArgumentException("Seul un administrateur peut lister les offres expirées");
        }

        List<OffreEmploi> offres = offreEmploiRepository.findByStatut(StatutOffre.EXPIREE);
        logger.info("Nombre total d'offres expirées récupérées : {}", offres.size());
        return offres;
    }

    @Transactional
    public List<OffreEmploi> listerMesOffresExpirees() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Liste des offres expirées demandée par l'utilisateur : {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé : {}", email);
                    return new IllegalArgumentException("Utilisateur non trouvé");
                });

        if (user.getRole() != RoleUtilisateur.RECRUTEUR) {
            logger.error("L'utilisateur {} n'est pas autorisé à lister ses offres expirées", email);
            throw new IllegalArgumentException("Seul un recruteur peut lister ses propres offres expirées");
        }

        List<OffreEmploi> offres = offreEmploiRepository.findByUserAndStatut(user, StatutOffre.EXPIREE);
        logger.info("Nombre d'offres expirées récupérées pour l'utilisateur {} : {}", email, offres.size());
        return offres;
    }
}