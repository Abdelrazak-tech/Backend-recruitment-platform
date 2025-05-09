package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Candidat;
import com.example.demo.entity.Recruteur;
import com.example.demo.entity.RoleUtilisateur;
import com.example.demo.entity.User;
import com.example.demo.repository.CandidatRepository;
import com.example.demo.repository.RecruteurRepository;
import com.example.demo.repository.UserRepository;

@Service
public class AdministrateurService {

    private static final Logger logger = LoggerFactory.getLogger(AdministrateurService.class);

    private final UserRepository userRepository;
    private final CandidatRepository candidatRepository;
    private final RecruteurRepository recruteurRepository;

    @Autowired
    public AdministrateurService(UserRepository userRepository,
                                 CandidatRepository candidatRepository,
                                 RecruteurRepository recruteurRepository) {
        this.userRepository = userRepository;
        this.candidatRepository = candidatRepository;
        this.recruteurRepository = recruteurRepository;
    }

    // Lister tous les utilisateurs
    public List<User> listerTousLesUtilisateurs() {
        logger.info("Récupération de tous les utilisateurs");
        return userRepository.findAll();
    }

    // Rechercher un utilisateur par ID
    public User getUtilisateurById(Long userId) {
        logger.info("Récupération de l'utilisateur avec l'ID : {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé avec l'ID : {}", userId);
                    return new IllegalArgumentException("Utilisateur non trouvé");
                });
    }

    // Rechercher des utilisateurs par email
    public Optional<User> rechercherParEmail(String email) {
        logger.info("Recherche d'un utilisateur avec l'email : {}", email);
        return userRepository.findByEmail(email);
    }

    // Rechercher des utilisateurs par rôle
    public List<User> rechercherParRole(RoleUtilisateur role) {
        logger.info("Recherche des utilisateurs avec le rôle : {}", role);
        return userRepository.findByRole(role);
    }

    // Activer ou désactiver un compte utilisateur
    public void activerDesactiverCompte(Long userId, boolean actif) {
        logger.info("Modification de l'état du compte pour l'utilisateur ID : {}, actif : {}", userId, actif);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé avec l'ID : {}", userId);
                    return new IllegalArgumentException("Utilisateur non trouvé");
                });

        // Ne pas désactiver un administrateur si c'est le dernier actif
        if (!actif && user.getRole() == RoleUtilisateur.ADMINISTRATEUR) {
            long adminActifs = userRepository.findByRole(RoleUtilisateur.ADMINISTRATEUR)
                    .stream()
                    .filter(User::isCompteActif)
                    .count();
            if (adminActifs <= 1) {
                logger.error("Impossible de désactiver le dernier administrateur actif");
                throw new IllegalStateException("Impossible de désactiver le dernier administrateur actif");
            }
        }

        user.setCompteActif(actif);
        userRepository.save(user);
        logger.info("Compte de l'utilisateur ID : {} mis à jour avec succès", userId);
    }

    // Modifier le rôle d'un utilisateur
    public void modifierRoleUtilisateur(Long userId, RoleUtilisateur nouveauRole) {
        logger.info("Modification du rôle de l'utilisateur ID : {} en {}", userId, nouveauRole);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé avec l'ID : {}", userId);
                    return new IllegalArgumentException("Utilisateur non trouvé");
                });

        // Ne pas modifier le rôle du dernier administrateur actif
        if (user.getRole() == RoleUtilisateur.ADMINISTRATEUR && nouveauRole != RoleUtilisateur.ADMINISTRATEUR) {
            long adminActifs = userRepository.findByRole(RoleUtilisateur.ADMINISTRATEUR)
                    .stream()
                    .filter(User::isCompteActif)
                    .count();
            if (adminActifs <= 1) {
                logger.error("Impossible de modifier le rôle du dernier administrateur actif");
                throw new IllegalStateException("Impossible de modifier le rôle du dernier administrateur actif");
            }
        }

        user.setRole(nouveauRole);
        userRepository.save(user);
        logger.info("Rôle de l'utilisateur ID : {} mis à jour avec succès", userId);
    }

    // Supprimer un utilisateur
    public void supprimerUtilisateur(Long userId) {
        logger.info("Suppression de l'utilisateur avec l'ID : {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé avec l'ID : {}", userId);
                    return new IllegalArgumentException("Utilisateur non trouvé");
                });

        // Ne pas supprimer le dernier administrateur actif
        if (user.getRole() == RoleUtilisateur.ADMINISTRATEUR) {
            long adminActifs = userRepository.findByRole(RoleUtilisateur.ADMINISTRATEUR)
                    .stream()
                    .filter(User::isCompteActif)
                    .count();
            if (adminActifs <= 1) {
                logger.error("Impossible de supprimer le dernier administrateur actif");
                throw new IllegalStateException("Impossible de supprimer le dernier administrateur actif");
            }
        }

        // Supprimer selon le type d'utilisateur
        if (user instanceof Candidat) {
            candidatRepository.deleteById(userId);
        } else if (user instanceof Recruteur) {
            recruteurRepository.deleteById(userId);
        } else {
            userRepository.deleteById(userId);
        }
        logger.info("Utilisateur ID : {} supprimé avec succès", userId);
    }

    // Lister les recruteurs en attente de validation
    public List<Recruteur> listerRecruteursEnAttente() {
        logger.info("Récupération des recruteurs en attente de validation");
        return recruteurRepository.findByStatutValidation("EN_ATTENTE");
    }

    // Valider un recruteur
    public void validerRecruteur(Long recruteurId) {
        logger.info("Validation du recruteur avec l'ID : {}", recruteurId);
        Recruteur recruteur = recruteurRepository.findById(recruteurId)
                .orElseThrow(() -> {
                    logger.error("Recruteur non trouvé avec l'ID : {}", recruteurId);
                    return new IllegalArgumentException("Recruteur non trouvé");
                });

        if (!recruteur.getStatutValidation().equals("EN_ATTENTE")) {
            logger.error("Le recruteur ID : {} n'est pas en attente de validation", recruteurId);
            throw new IllegalStateException("Le recruteur n'est pas en attente de validation");
        }

        recruteur.setStatutValidation("VALIDE");
        recruteur.setCompteActif(true);
        recruteurRepository.save(recruteur);
        logger.info("Recruteur ID : {} validé avec succès", recruteurId);
    }

    // Rejeter un recruteur
    public void rejeterRecruteur(Long recruteurId) {
        logger.info("Rejet du recruteur avec l'ID : {}", recruteurId);
        Recruteur recruteur = recruteurRepository.findById(recruteurId)
                .orElseThrow(() -> {
                    logger.error("Recruteur non trouvé avec l'ID : {}", recruteurId);
                    return new IllegalArgumentException("Recruteur non trouvé");
                });

        if (!recruteur.getStatutValidation().equals("EN_ATTENTE")) {
            logger.error("Le recruteur ID : {} n'est pas en attente de validation", recruteurId);
            throw new IllegalStateException("Le recruteur n'est pas en attente de validation");
        }

        recruteur.setStatutValidation("REJETE");
        recruteur.setCompteActif(false);
        recruteurRepository.save(recruteur);
        logger.info("Recruteur ID : {} rejeté avec succès", recruteurId);
    }

    // Récupérer le fichier Kbis d'un recruteur
    public byte[] getFichierKbis(Long recruteurId) {
        logger.info("Récupération du fichier Kbis pour le recruteur ID : {}", recruteurId);
        Recruteur recruteur = recruteurRepository.findById(recruteurId)
                .orElseThrow(() -> {
                    logger.error("Recruteur non trouvé avec l'ID : {}", recruteurId);
                    return new IllegalArgumentException("Recruteur non trouvé");
                });
        return recruteur.getFichierKbis();
    }
}