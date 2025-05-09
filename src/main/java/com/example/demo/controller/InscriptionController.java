package com.example.demo.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.AdministrateurInscriptionDTO;
import com.example.demo.dto.CandidatInscriptionDTO;
import com.example.demo.dto.RecruteurInscriptionDTO;
import com.example.demo.entity.Candidat;
import com.example.demo.service.InscriptionService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inscription")
public class InscriptionController {

    private final InscriptionService inscriptionService;

    public InscriptionController(InscriptionService inscriptionService) {
        this.inscriptionService = inscriptionService;
    }

    @PostMapping("/candidat")
    public ResponseEntity<String> inscrireCandidat(@Valid @RequestBody CandidatInscriptionDTO dto) {
        try {
            Candidat candidat = inscriptionService.inscrireCandidat(dto);
            return ResponseEntity.ok("Candidat inscrit avec succès : " + candidat.getEmail() + ". Veuillez vérifier votre email.");
        } catch (MessagingException e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'envoi de l'email : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/recruteur", consumes = "multipart/form-data")
    public ResponseEntity<String> inscrireRecruteur(
            @ModelAttribute @Valid RecruteurInscriptionDTO dto,
            @RequestPart("fichierKbis") MultipartFile fichierKbis) {
        try {
            dto.setFichierKbis(fichierKbis);
            inscriptionService.inscrireRecruteur(dto);
            return ResponseEntity.ok("Demande d'inscription du recruteur soumise avec succès. En attente de validation.");
        } catch (MessagingException e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'envoi de l'email : " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Erreur lors de la lecture du fichier Kbis : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/administrateur", consumes = "application/json")
    public ResponseEntity<String> inscrireAdministrateur(@Valid @RequestBody AdministrateurInscriptionDTO dto) {
        try {
            inscriptionService.inscrireAdministrateur(dto);
            return ResponseEntity.ok("Administrateur inscrit avec succès. Veuillez vérifier votre email.");
        } catch (MessagingException e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'envoi de l'email : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verifier-email")
    public ResponseEntity<String> verifierEmail(@RequestParam String token) {
        try {
            inscriptionService.verifierEmail(token);
            return ResponseEntity.ok("Email vérifié avec succès");
        } catch (MessagingException e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'envoi de l'email de vérification : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}