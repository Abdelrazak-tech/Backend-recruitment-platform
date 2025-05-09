package com.example.demo.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.DemandeAdminDTO;
import com.example.demo.dto.DemandeOffreDTO;
import com.example.demo.entity.Administrateur;
import com.example.demo.entity.Candidat;
import com.example.demo.entity.DemandeAdmin;
import com.example.demo.entity.DemandeOffre;
import com.example.demo.entity.OffreEmploi;
import com.example.demo.entity.Recruteur;
import com.example.demo.entity.User;
import com.example.demo.repository.DemandeAdminRepository;
import com.example.demo.repository.DemandeOffreRepository;
import com.example.demo.repository.OffreEmploiRepository;
import com.example.demo.repository.UserRepository;

@Service
public class DemandeService {

    private static final Logger logger = LoggerFactory.getLogger(DemandeService.class);

    private final DemandeAdminRepository demandeAdminRepository;
    private final DemandeOffreRepository demandeOffreRepository;
    private final OffreEmploiRepository offreEmploiRepository;
    private final UserRepository userRepository;

    public DemandeService(DemandeAdminRepository demandeAdminRepository, DemandeOffreRepository demandeOffreRepository, OffreEmploiRepository offreEmploiRepository, UserRepository userRepository) {
        this.demandeAdminRepository = demandeAdminRepository;
        this.demandeOffreRepository = demandeOffreRepository;
        this.offreEmploiRepository = offreEmploiRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void traiterDemandeAdmin(DemandeAdminDTO dto) throws IOException {
        logger.info("Traitement de la demande admin : email = {}, sujet = {}", dto.getEmail(), dto.getSujet());

        DemandeAdmin demande = new DemandeAdmin();
        demande.setEmail(dto.getEmail());
        demande.setNom(dto.getNom());
        demande.setSujet(dto.getSujet());
        demande.setMessage(dto.getMessage());

        if (dto.getPieceJointe() != null && !dto.getPieceJointe().isEmpty()) { // Optionnel
            if (!dto.getPieceJointe().getContentType().equals("application/pdf")) {
                throw new IllegalArgumentException("La pièce jointe doit être un PDF");
            }
            long maxFileSize = 5 * 1024 * 1024;
            if (dto.getPieceJointe().getSize() > maxFileSize) {
                throw new IllegalArgumentException("La pièce jointe est trop volumineuse (max 5MB)");
            }
            demande.setPieceJointe(dto.getPieceJointe().getBytes());
        }

        demandeAdminRepository.save(demande);
    }

    @Transactional
    public void traiterDemandeOffre(Long offreEmploiId, DemandeOffreDTO dto) throws IOException {
        logger.info("Traitement de la demande offre : offreId = {}", offreEmploiId);

        OffreEmploi offreEmploi = offreEmploiRepository.findById(offreEmploiId)
                .orElseThrow(() -> new IllegalArgumentException("Offre d'emploi non trouvée"));

        String emailCandidat = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(emailCandidat)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        if (!(user instanceof Candidat)) {
            throw new IllegalArgumentException("Seul un candidat peut soumettre une demande");
        }
        Candidat candidat = (Candidat) user;

        DemandeOffre demande = new DemandeOffre();
        demande.setOffreEmploi(offreEmploi);
        demande.setCandidat(candidat);
        demande.setSujet(dto.getSujet());
        demande.setMessage(dto.getMessage());

        if (dto.getPieceJointe() != null && !dto.getPieceJointe().isEmpty()) { // Optionnel
            if (!dto.getPieceJointe().getContentType().equals("application/pdf")) {
                throw new IllegalArgumentException("La pièce jointe doit être un PDF");
            }
            long maxFileSize = 5 * 1024 * 1024;
            if (dto.getPieceJointe().getSize() > maxFileSize) {
                throw new IllegalArgumentException("La pièce jointe est trop volumineux (max 5MB)");
            }
            demande.setPieceJointe(dto.getPieceJointe().getBytes());
        }

        demandeOffreRepository.save(demande);
    }

    @Transactional(readOnly = true)
    public List<DemandeOffre> getDemandesForAuteur() {
        String emailAuteur = SecurityContextHolder.getContext().getAuthentication().getName();
        User auteur = userRepository.findByEmail(emailAuteur)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        if (!(auteur instanceof Recruteur || auteur instanceof Administrateur)) {
            throw new IllegalArgumentException("Seul un recruteur ou un admin peut voir les demandes");
        }

        List<OffreEmploi> offreEmplois = offreEmploiRepository.findByUser(auteur);
        return demandeOffreRepository.findByOffreEmploiIn(offreEmplois);
    }
}