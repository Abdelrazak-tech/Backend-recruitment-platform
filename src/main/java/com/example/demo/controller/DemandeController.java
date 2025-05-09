package com.example.demo.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.DemandeAdminDTO;
import com.example.demo.service.DemandeService;

@RestController
@RequestMapping("/api")
public class DemandeController {

    private final DemandeService demandeService;

    public DemandeController(DemandeService demandeService) {
        this.demandeService = demandeService;
    }

    @PostMapping("/contact/admin")
    public ResponseEntity<String> soumettreDemandeAdmin(@ModelAttribute DemandeAdminDTO dto) {
        try {
            if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body("L'email est requis");
            }
            if (dto.getMessage() == null || dto.getMessage().isEmpty()) {
                return ResponseEntity.badRequest().body("Le message est requis");
            }
            demandeService.traiterDemandeAdmin(dto);
            return ResponseEntity.ok("Demande soumise avec succès à l'admin");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Erreur lors de la lecture de la pièce jointe : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Une erreur inattendue s'est produite : " + e.getMessage());
        }
    }
}
