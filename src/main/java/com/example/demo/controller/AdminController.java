package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.OffreEmploiDTO;
import com.example.demo.entity.Candidat;
import com.example.demo.entity.Candidature;
import com.example.demo.entity.DemandeOffre;
import com.example.demo.entity.Notification;
import com.example.demo.entity.OffreEmploi;
import com.example.demo.entity.Recruteur;
import com.example.demo.entity.RoleUtilisateur;
import com.example.demo.entity.User;
import com.example.demo.repository.CandidatureRepository;
import com.example.demo.repository.OffreEmploiRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AdministrateurService;
import com.example.demo.service.DemandeService;
import com.example.demo.service.NotificationService;
import com.example.demo.service.OffreEmploiService;

@RestController
@RequestMapping("/api/dashboard/admin")
public class AdminController {

    private final AdministrateurService administrateurService;
    private final OffreEmploiService offreEmploiService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final OffreEmploiRepository offreEmploiRepository;
    private final CandidatureRepository candidatureRepository;
    private final DemandeService demandeService;

    public AdminController(AdministrateurService administrateurService, 
                           OffreEmploiService offreEmploiService,
                           NotificationService notificationService,
                           UserRepository userRepository,
                           OffreEmploiRepository offreEmploiRepository,
                           CandidatureRepository candidatureRepository, DemandeService demandeService) {
        this.administrateurService = administrateurService;
        this.offreEmploiService = offreEmploiService;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.offreEmploiRepository = offreEmploiRepository;
        this.candidatureRepository = candidatureRepository;
        this.demandeService = demandeService;
    }

    // Lister tous les utilisateurs
    @GetMapping("/utilisateurs")
    public ResponseEntity<List<User>> listerTousLesUtilisateurs() {
        List<User> utilisateurs = administrateurService.listerTousLesUtilisateurs();
        return ResponseEntity.ok(utilisateurs);
    }

    // Récupérer un utilisateur par ID
    @GetMapping("/utilisateurs/{id}")
    public ResponseEntity<User> getUtilisateurById(@PathVariable Long id) {
        User user = administrateurService.getUtilisateurById(id);
        return ResponseEntity.ok(user);
    }

    // Rechercher un utilisateur par email
    @GetMapping("/utilisateurs/rechercher-par-email")
    public ResponseEntity<User> rechercherParEmail(@RequestParam String email) {
        Optional<User> user = administrateurService.rechercherParEmail(email);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Rechercher des utilisateurs par rôle
    @GetMapping("/utilisateurs/rechercher-par-role")
    public ResponseEntity<List<User>> rechercherParRole(@RequestParam RoleUtilisateur role) {
        List<User> utilisateurs = administrateurService.rechercherParRole(role);
        return ResponseEntity.ok(utilisateurs);
    }

    // Activer un compte utilisateur
    @PutMapping("/utilisateurs/{id}/activer")
    public ResponseEntity<String> activerCompte(@PathVariable Long id) {
        administrateurService.activerDesactiverCompte(id, true);
        return ResponseEntity.ok("Compte activé avec succès");
    }

    // Désactiver un compte utilisateur
    @PutMapping("/utilisateurs/{id}/desactiver")
    public ResponseEntity<String> desactiverCompte(@PathVariable Long id) {
        administrateurService.activerDesactiverCompte(id, false);
        return ResponseEntity.ok("Compte désactivé avec succès");
    }

    // Modifier le rôle d'un utilisateur
    @PutMapping("/utilisateurs/{id}/role")
    public ResponseEntity<String> modifierRoleUtilisateur(@PathVariable Long id, @RequestParam RoleUtilisateur role) {
        administrateurService.modifierRoleUtilisateur(id, role);
        return ResponseEntity.ok("Rôle modifié avec succès");
    }

    // Supprimer un utilisateur
    @DeleteMapping("/utilisateurs/{id}")
    public ResponseEntity<String> supprimerUtilisateur(@PathVariable Long id) {
        administrateurService.supprimerUtilisateur(id);
        return ResponseEntity.ok("Utilisateur supprimé avec succès");
    }

    // Lister les recruteurs en attente de validation
    @GetMapping("/recruteurs/en-attente")
    public ResponseEntity<List<Recruteur>> listerRecruteursEnAttente() {
        List<Recruteur> recruteurs = administrateurService.listerRecruteursEnAttente();
        return ResponseEntity.ok(recruteurs);
    }

    // Valider un recruteur
    @PutMapping("/recruteurs/{id}/valider")
    public ResponseEntity<String> validerRecruteur(@PathVariable Long id) {
        administrateurService.validerRecruteur(id);
        return ResponseEntity.ok("Recruteur validé avec succès");
    }

    // Rejeter un recruteur
    @PutMapping("/recruteurs/{id}/rejeter")
    public ResponseEntity<String> rejeterRecruteur(@PathVariable Long id) {
        administrateurService.rejeterRecruteur(id);
        return ResponseEntity.ok("Recruteur rejeté avec succès");
    }

    // Télécharger le fichier Kbis d'un recruteur
    @GetMapping("/recruteurs/{id}/fichier-kbis")
    public ResponseEntity<byte[]> getFichierKbis(@PathVariable Long id) {
        byte[] fichierKbis = administrateurService.getFichierKbis(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"kbis-" + id + ".pdf\"")
                .body(fichierKbis);
    }

    // Lister toutes les offres d'emploi
    @GetMapping("/offres-emploi")
    public ResponseEntity<List<OffreEmploi>> listerToutesLesOffres() {
        List<OffreEmploi> offres = offreEmploiService.listerToutesLesOffres();
        return ResponseEntity.ok(offres);
    }

    // Créer une offre d'emploi
    @PostMapping("/offres-emploi")
    public ResponseEntity<String> creerOffreEmploi(@RequestBody OffreEmploiDTO dto) {
        try {
            OffreEmploi offreEmploi = offreEmploiService.creerOffreEmploi(dto);
            return ResponseEntity.ok("Offre d'emploi créée avec succès : ID " + offreEmploi.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mes-demandes-offres")
    public ResponseEntity<List<DemandeOffre>> getMesDemandesOffres() {
        try {
            List<DemandeOffre> demandes = demandeService.getDemandesForAuteur();
            return ResponseEntity.ok(demandes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Modifier une offre d'emploi
    @PutMapping("/offres-emploi/{offreId}")
    public ResponseEntity<String> modifierOffreEmploi(@PathVariable Long offreId, @RequestBody OffreEmploiDTO dto) {
        try {
            OffreEmploi offreEmploi = offreEmploiService.modifierOffreEmploi(offreId, dto);
            return ResponseEntity.ok("Offre d'emploi modifiée avec succès : ID " + offreEmploi.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Supprimer une offre d'emploi
    @DeleteMapping("/offres-emploi/{offreId}")
    public ResponseEntity<String> supprimerOffreEmploi(@PathVariable Long offreId) {
        try {
            offreEmploiService.supprimerOffreEmploi(offreId);
            return ResponseEntity.ok("Offre d'emploi supprimée avec succès : ID " + offreId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Prolonger une offre d'emploi
    @PostMapping("/offres-emploi/{offreId}/prolonger")
    public ResponseEntity<String> prolongerOffreEmploi(@PathVariable Long offreId, @RequestBody OffreEmploiDTO dto) {
        try {
            OffreEmploi offreEmploi = offreEmploiService.prolongerOffreEmploi(offreId, dto.isProlonger());
            if (dto.isProlonger()) {
                return ResponseEntity.ok("Offre d'emploi prolongée avec succès : ID " + offreEmploi.getId());
            } else {
                return ResponseEntity.ok("Offre d'emploi supprimée : ID " + offreId);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Lister les offres expirées
    @GetMapping("/offres-emploi/expirees")
    public ResponseEntity<List<OffreEmploi>> listerOffresExpirees() {
        List<OffreEmploi> offres = offreEmploiService.listerOffresExpirees();
        return ResponseEntity.ok(offres);
    }

    // Récupérer les notifications non lues de l'utilisateur connecté
    @GetMapping("/notifications/unread")
    public ResponseEntity<List<Notification>> getNotificationsNonLues() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        List<Notification> notifications = notificationService.getNotificationsNonLues(user);
        return ResponseEntity.ok(notifications);
    }

    // Récupérer toutes les notifications de l'utilisateur connecté
    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getToutesNotifications() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        List<Notification> notifications = notificationService.getToutesNotifications(user);
        return ResponseEntity.ok(notifications);
    }

    // Marquer une notification comme lue
    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<Void> marquerCommeLu(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        // Vérifier que la notification appartient à l'utilisateur connecté
        Notification notification = notificationService.getToutesNotifications(user).stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Notification non trouvée ou non autorisée"));
        
        notificationService.marquerCommeLu(id);
        return ResponseEntity.ok().build();
    }

    // Lister les candidatures pour une offre spécifique
    @GetMapping("/offres-emploi/{offreId}/candidatures")
    public ResponseEntity<List<Candidature>> listerCandidaturesParOffre(@PathVariable Long offreId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        // Vérifier que l'utilisateur est un administrateur
        if (user.getRole() != RoleUtilisateur.ADMINISTRATEUR) {
            throw new IllegalArgumentException("Seul un administrateur peut voir les candidatures");
        }

        OffreEmploi offreEmploi = offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> new IllegalArgumentException("Offre d'emploi non trouvée"));

        List<Candidature> candidatures = candidatureRepository.findByOffreEmploi(offreEmploi);
        return ResponseEntity.ok(candidatures);
    }

    // Télécharger le CV d’une candidature
    @GetMapping("/candidatures/{candidatureId}/cv")
    public ResponseEntity<byte[]> telechargerCvCandidature(@PathVariable Long candidatureId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        // Vérifier que l'utilisateur est un administrateur
        if (user.getRole() != RoleUtilisateur.ADMINISTRATEUR) {
            throw new IllegalArgumentException("Seul un administrateur peut télécharger le CV");
        }

        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new IllegalArgumentException("Candidature non trouvée"));

        if (candidature.getCv() == null || candidature.getCv().length == 0) {
            return ResponseEntity.notFound().build();
        }

        Candidat candidat = candidature.getCandidat();
        String fileName = "cv-" + candidat.getPrenom() + "-" + candidat.getNom() + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(candidature.getCv());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}