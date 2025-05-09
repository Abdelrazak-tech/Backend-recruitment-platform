package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.OffreEmploiDTO;
import com.example.demo.entity.Candidat;
import com.example.demo.entity.Candidature;
import com.example.demo.entity.DemandeOffre;
import com.example.demo.entity.OffreEmploi;
import com.example.demo.entity.RoleUtilisateur;
import com.example.demo.entity.User;
import com.example.demo.repository.CandidatureRepository;
import com.example.demo.repository.OffreEmploiRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.DemandeService;
import com.example.demo.service.OffreEmploiService;

@RestController
@RequestMapping("/api/dashboard/recruteur")
public class RecruteurController {

    private final OffreEmploiService offreEmploiService;
    private final UserRepository userRepository;
    private final OffreEmploiRepository offreEmploiRepository;
    private final CandidatureRepository candidatureRepository;
    private final DemandeService demandeService;

    public RecruteurController(OffreEmploiService offreEmploiService,
                               UserRepository userRepository,
                               OffreEmploiRepository offreEmploiRepository,
                               CandidatureRepository candidatureRepository, DemandeService demandeService) {
        this.offreEmploiService = offreEmploiService;
        this.userRepository = userRepository;
        this.offreEmploiRepository = offreEmploiRepository;
        this.candidatureRepository = candidatureRepository;
        this.demandeService = demandeService;
        
    }

    @GetMapping
    public ResponseEntity<String> getRecruteurDashboard() {
        return ResponseEntity.ok("Bienvenue sur le tableau de bord du recruteur !");
    }

    // Lister les offres d'emploi publiées par le recruteur connecté
    @Transactional(readOnly = true)
    @GetMapping("/offres-emploi")
    public ResponseEntity<List<OffreEmploi>> listerOffresParUtilisateur() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

            // Vérifier que l'utilisateur est un recruteur
            if (user.getRole() != RoleUtilisateur.RECRUTEUR) {
                throw new IllegalArgumentException("Seul un recruteur peut voir ses offres");
            }

            List<OffreEmploi> offres = offreEmploiService.listerOffresParUtilisateur(user);
            return ResponseEntity.ok(offres);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Créer une offre d'emploi
    @Transactional
    @PostMapping("/offres-emploi")
    public ResponseEntity<String> creerOffreEmploi(@RequestBody OffreEmploiDTO dto) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

            // Vérifier que l'utilisateur est un recruteur
            if (user.getRole() != RoleUtilisateur.RECRUTEUR) {
                throw new IllegalArgumentException("Seul un recruteur peut créer une offre");
            }

            OffreEmploi offreEmploi = offreEmploiService.creerOffreEmploi(dto);
            return ResponseEntity.ok("Offre d'emploi créée avec succès : ID " + offreEmploi.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la création de l'offre : " + e.getMessage());
        }
    }

    // Modifier une offre d'emploi
    @Transactional
    @PutMapping("/offres-emploi/{offreId}")
    public ResponseEntity<String> modifierOffreEmploi(@PathVariable Long offreId, @RequestBody OffreEmploiDTO dto) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

            // Vérifier que l'utilisateur est un recruteur
            if (user.getRole() != RoleUtilisateur.RECRUTEUR) {
                throw new IllegalArgumentException("Seul un recruteur peut modifier une offre");
            }

            // Vérifier que l’offre appartient au recruteur connecté
            OffreEmploi offreEmploi = offreEmploiRepository.findById(offreId)
                    .orElseThrow(() -> new IllegalArgumentException("Offre d'emploi non trouvée"));
            if (!offreEmploi.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier cette offre");
            }

            OffreEmploi updatedOffre = offreEmploiService.modifierOffreEmploi(offreId, dto);
            return ResponseEntity.ok("Offre d'emploi modifiée avec succès : ID " + updatedOffre.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la modification de l'offre : " + e.getMessage());
        }
    }

    // Supprimer une offre d'emploi
    @Transactional
    @DeleteMapping("/offres-emploi/{offreId}")
    public ResponseEntity<String> supprimerOffreEmploi(@PathVariable Long offreId) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

            // Vérifier que l'utilisateur est un recruteur
            if (user.getRole() != RoleUtilisateur.RECRUTEUR) {
                throw new IllegalArgumentException("Seul un recruteur peut supprimer une offre");
            }

            // Vérifier que l’offre appartient au recruteur connecté
            OffreEmploi offreEmploi = offreEmploiRepository.findById(offreId)
                    .orElseThrow(() -> new IllegalArgumentException("Offre d'emploi non trouvée"));
            if (!offreEmploi.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Vous n'êtes pas autorisé à supprimer cette offre");
            }

            offreEmploiService.supprimerOffreEmploi(offreId);
            return ResponseEntity.ok("Offre d'emploi supprimée avec succès : ID " + offreId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la suppression de l'offre : " + e.getMessage());
        }
    }

    @GetMapping("/mes-demandes-offres")
    @PreAuthorize("hasRole('RECRUTEUR') or hasRole('ADMIN')")
    public ResponseEntity<List<DemandeOffre>> getMesDemandesOffres() {
        try {
            List<DemandeOffre> demandes = demandeService.getDemandesForAuteur();
            return ResponseEntity.ok(demandes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Lister les offres expirées publiées par le recruteur connecté
    @Transactional(readOnly = true)
    @GetMapping("/offres-emploi/expirees")
    public ResponseEntity<List<OffreEmploi>> listerOffresExpireesParUtilisateur() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

            // Vérifier que l'utilisateur est un recruteur
            if (user.getRole() != RoleUtilisateur.RECRUTEUR) {
                throw new IllegalArgumentException("Seul un recruteur peut voir ses offres expirées");
            }

            List<OffreEmploi> offres = offreEmploiService.listerOffresExpireesParUtilisateur(user);
            return ResponseEntity.ok(offres);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Prolonger une offre d'emploi
    @Transactional
    @PostMapping("/offres-emploi/{offreId}/prolonger")
    public ResponseEntity<String> prolongerOffreEmploi(@PathVariable Long offreId, @RequestBody OffreEmploiDTO dto) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

            // Vérifier que l'utilisateur est un recruteur
            if (user.getRole() != RoleUtilisateur.RECRUTEUR) {
                throw new IllegalArgumentException("Seul un recruteur peut prolonger une offre");
            }

            // Vérifier que l’offre appartient au recruteur connecté
            OffreEmploi offreEmploi = offreEmploiRepository.findById(offreId)
                    .orElseThrow(() -> new IllegalArgumentException("Offre d'emploi non trouvée"));
            if (!offreEmploi.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Vous n'êtes pas autorisé à prolonger cette offre");
            }

            OffreEmploi updatedOffre = offreEmploiService.prolongerOffreEmploi(offreId, dto.isProlonger());
            if (dto.isProlonger()) {
                return ResponseEntity.ok("Offre d'emploi prolongée avec succès : ID " + updatedOffre.getId());
            } else {
                return ResponseEntity.ok("Offre d'emploi supprimée : ID " + offreId);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la prolongation de l'offre : " + e.getMessage());
        }
    }

    // Lister les candidatures pour une offre spécifique (uniquement pour les offres du recruteur connecté)
    @GetMapping("/offres-emploi/{offreId}/candidatures")
    public ResponseEntity<List<Candidature>> listerCandidaturesParOffre(@PathVariable Long offreId) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

            // Vérifier que l'utilisateur est un recruteur
            if (user.getRole() != RoleUtilisateur.RECRUTEUR) {
                throw new IllegalArgumentException("Seul un recruteur peut voir les candidatures");
            }

            // Récupérer l’offre et vérifier qu’elle appartient au recruteur connecté
            OffreEmploi offreEmploi = offreEmploiRepository.findById(offreId)
                    .orElseThrow(() -> new IllegalArgumentException("Offre d'emploi non trouvée"));

            if (!offreEmploi.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Vous n'êtes pas autorisé à voir les candidatures de cette offre");
            }

            List<Candidature> candidatures = candidatureRepository.findByOffreEmploi(offreEmploi);
            return ResponseEntity.ok(candidatures);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Télécharger le CV d’une candidature (uniquement pour les offres du recruteur connecté)
    @GetMapping("/candidatures/{candidatureId}/cv")
    public ResponseEntity<byte[]> telechargerCvCandidature(@PathVariable Long candidatureId) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

            // Vérifier que l'utilisateur est un recruteur
            if (user.getRole() != RoleUtilisateur.RECRUTEUR) {
                throw new IllegalArgumentException("Seul un recruteur peut télécharger le CV");
            }

            // Récupérer la candidature
            Candidature candidature = candidatureRepository.findById(candidatureId)
                    .orElseThrow(() -> new IllegalArgumentException("Candidature non trouvée"));

            // Vérifier que l’offre associée appartient au recruteur connecté
            OffreEmploi offreEmploi = candidature.getOffreEmploi();
            if (!offreEmploi.getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Vous n'êtes pas autorisé à accéder à cette candidature");
            }

            if (candidature.getCv() == null || candidature.getCv().length == 0) {
                return ResponseEntity.notFound().build();
            }

            Candidat candidat = candidature.getCandidat();
            String fileName = "cv-" + candidat.getPrenom() + "-" + candidat.getNom() + ".pdf";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(candidature.getCv());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}