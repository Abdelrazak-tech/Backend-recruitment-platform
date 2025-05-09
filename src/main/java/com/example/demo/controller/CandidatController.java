package com.example.demo.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CandidatProfilResponseDTO;
import com.example.demo.dto.CandidatUpdateDTO;
import com.example.demo.dto.DemandeOffreDTO;
import com.example.demo.entity.Candidat;
import com.example.demo.entity.Candidature;
import com.example.demo.service.CandidatureService;
import com.example.demo.service.DemandeService;
import com.example.demo.service.InscriptionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/dashboard/candidat")
public class CandidatController {

    private static final Logger logger = LoggerFactory.getLogger(CandidatController.class);

    private final CandidatureService candidatureService;
    private final InscriptionService inscriptionService;
    private final DemandeService demandeService;

    public CandidatController(CandidatureService candidatureService, InscriptionService inscriptionService, DemandeService demandeService) {
        this.candidatureService = candidatureService;
        this.inscriptionService = inscriptionService;
        this.demandeService = demandeService;
    }

    @GetMapping("/profil")
    public ResponseEntity<CandidatProfilResponseDTO> getProfil() {
        try {
            String emailCandidat = SecurityContextHolder.getContext().getAuthentication().getName();
            Candidat candidat = inscriptionService.getCandidatProfileByEmail(emailCandidat);
            CandidatProfilResponseDTO responseDTO = new CandidatProfilResponseDTO(candidat);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du profil candidat", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping(value = "/profil", consumes = "multipart/form-data")
    public ResponseEntity<String> updateProfil(@Valid @RequestBody CandidatUpdateDTO dto) {
        try {
            String emailCandidat = SecurityContextHolder.getContext().getAuthentication().getName();
            Candidat candidat = inscriptionService.updateCandidatProfileByEmail(emailCandidat, dto, dto.getCvFile());
            return ResponseEntity.ok("Profil et CV mis à jour avec succès pour le candidat : " + candidat.getEmail());
        } catch (IllegalArgumentException e) {
            logger.warn("Erreur de validation du profil", e);
            return ResponseEntity.badRequest().body("Erreur lors de la mise à jour du profil : " + e.getMessage());
        } catch (IOException e) {
            logger.error("Erreur d'entrée/sortie lors du traitement du fichier CV", e);
            return ResponseEntity.badRequest().body("Erreur lors de la lecture du fichier CV : " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la mise à jour du profil", e);
            return ResponseEntity.badRequest().body("Une erreur inattendue s'est produite : " + e.getMessage());
        }
    }

    @PostMapping("/offres/{offreId}/postuler")
    public ResponseEntity<String> postulerOffre(@PathVariable Long offreId) {
        try {
            String emailCandidat = SecurityContextHolder.getContext().getAuthentication().getName();
            Candidature candidature = candidatureService.postulerOffre(offreId, emailCandidat);
            return ResponseEntity.ok("Candidature soumise avec succès : ID " + candidature.getId());
        } catch (Exception e) {
            logger.error("Erreur lors de la soumission de la candidature", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/offreEmploi/{offreEmploiId}/renseignements")
    public ResponseEntity<String> soumettreDemandeOffre(
            @PathVariable Long offreEmploiId,
            @Valid @RequestBody DemandeOffreDTO demandeOffreDTO) throws IOException {
        logger.info("Reçu demande pour offre : offreId = {}", offreEmploiId);
        demandeService.traiterDemandeOffre(offreEmploiId, demandeOffreDTO);
        return ResponseEntity.ok("Demande soumise avec succès pour l'offre");
    }
}