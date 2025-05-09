package com.example.demo.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Util.EmailValidator;
import com.example.demo.Util.PasswordValidator;
import com.example.demo.dto.AdministrateurInscriptionDTO;
import com.example.demo.dto.CandidatInscriptionDTO;
import com.example.demo.dto.CandidatUpdateDTO;
import com.example.demo.dto.RecruteurInscriptionDTO;
import com.example.demo.entity.Administrateur;
import com.example.demo.entity.Candidat;
import com.example.demo.entity.Certification;
import com.example.demo.entity.Experience;
import com.example.demo.entity.Formation;
import com.example.demo.entity.Projet;
import com.example.demo.entity.Recruteur;
import com.example.demo.entity.RoleUtilisateur;
import com.example.demo.entity.User;
import com.example.demo.entity.VerificationToken;
import com.example.demo.repository.AdministrateurRepository;
import com.example.demo.repository.CandidatRepository;
import com.example.demo.repository.RecruteurRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VerificationTokenRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class InscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(InscriptionService.class);

    private final CandidatRepository candidatRepository;
    private final RecruteurRepository recruteurRepository;
    private final AdministrateurRepository administrateurRepository;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public InscriptionService(CandidatRepository candidatRepository,
                              RecruteurRepository recruteurRepository,
                              AdministrateurRepository administrateurRepository,
                              UserRepository userRepository,
                              VerificationTokenRepository verificationTokenRepository,
                              BCryptPasswordEncoder passwordEncoder,
                              JavaMailSender mailSender) {
        this.candidatRepository = candidatRepository;
        this.recruteurRepository = recruteurRepository;
        this.administrateurRepository = administrateurRepository;
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Transactional
    public Candidat inscrireCandidat(CandidatInscriptionDTO dto) throws MessagingException {
        logger.info("Début de l'inscription pour le candidat avec email : {}", dto.getEmail());

        validateCommonFields(dto.getEmail(), dto.getMotDePasse(), dto.getEmailConfirmation(), dto.getMotDePasseConfirmation(), dto.getConditionsAcceptees());

        validateRequiredField(dto.getCivilite(), "La civilité est obligatoire");
        validateRequiredField(dto.getPrenom(), "Le prénom est obligatoire");
        validateRequiredField(dto.getNom(), "Le nom est obligatoire");
        validateRequiredField(dto.getTelephone(), "Le téléphone est obligatoire");
        if (dto.getDateNaissance() == null) {
            logger.error("La date de naissance est obligatoire pour : {}", dto.getEmail());
            throw new IllegalArgumentException("La date de naissance est obligatoire");
        }
        validateRequiredField(dto.getPays(), "Le pays est obligatoire");
        validateRequiredField(dto.getVille(), "La ville est obligatoire");
        validateRequiredField(dto.getDomaineEtudes(), "Le domaine d'études est obligatoire");
        validateRequiredField(dto.getTypeFormation(), "Le type de formation est obligatoire");
        validateRequiredField(dto.getNiveauEtudes(), "Le niveau d'études est obligatoire");

        checkEmailExists(dto.getEmail());

        Candidat candidat = new Candidat();
        candidat.setEmail(dto.getEmail().trim());
        String hashedPassword = passwordEncoder.encode(dto.getMotDePasse());
        candidat.setMotDePasse(hashedPassword);
        logger.info("Mot de passe haché pour le candidat {} : {}", dto.getEmail(), hashedPassword);

        candidat.setCivilite(dto.getCivilite());
        candidat.setPrenom(dto.getPrenom());
        candidat.setNom(dto.getNom());
        candidat.setTelephone(dto.getTelephone());
        candidat.setDateNaissance(dto.getDateNaissance());
        candidat.setPays(dto.getPays());
        candidat.setVille(dto.getVille());
        candidat.setDomaineEtudes(dto.getDomaineEtudes());
        candidat.setTypeFormation(dto.getTypeFormation());
        candidat.setNiveauEtudes(dto.getNiveauEtudes());
        candidat.setConditionsAcceptees(dto.getConditionsAcceptees());
        candidat.setRole(RoleUtilisateur.CANDIDAT);
        candidat.setEmailVerifie(false);
        candidat.setCompteActif(false);

        candidat = candidatRepository.save(candidat);
        logger.info("Candidat sauvegardé avec succès : {}", candidat.getEmail());

        envoyerEmailVerification(candidat.getEmail(), candidat.getId());
        return candidat;
    }

    @Transactional
    public void inscrireRecruteur(RecruteurInscriptionDTO dto) throws MessagingException, IOException {
        logger.info("Début de l'inscription pour le recruteur avec email : {}", dto.getEmail());

        validateCommonFields(dto.getEmail(), dto.getMotDePasse(), dto.getEmailConfirmation(), dto.getMotDePasseConfirmation(), dto.isConditionsAcceptees());

        validateRequiredField(dto.getRaisonSociale(), "La raison sociale est obligatoire");
        validateRequiredField(dto.getFormeJuridique(), "La forme juridique est obligatoire");
        validateRequiredField(dto.getNomContact(), "Le nom du contact est obligatoire");
        validateRequiredField(dto.getFonction(), "La fonction est obligatoire");
        validateRequiredField(dto.getTelephone(), "Le téléphone est obligatoire");
        validateRequiredField(dto.getPays(), "Le pays est obligatoire");
        validateRequiredField(dto.getVille(), "La ville est obligatoire");
        validateRequiredField(dto.getNumeroSiret(), "Le numéro SIRET est obligatoire");
        if (dto.getFichierKbis() == null || dto.getFichierKbis().isEmpty()) {
            logger.error("Le fichier Kbis est obligatoire pour : {}", dto.getEmail());
            throw new IllegalArgumentException("Le fichier Kbis est obligatoire");
        }

        checkEmailExists(dto.getEmail());

        Recruteur recruteur = new Recruteur();
        recruteur.setEmail(dto.getEmail().trim());
        String hashedPassword = passwordEncoder.encode(dto.getMotDePasse());
        recruteur.setMotDePasse(hashedPassword);
        logger.info("Mot de passe haché pour le recruteur {} : {}", dto.getEmail(), hashedPassword);

        recruteur.setRaisonSociale(dto.getRaisonSociale());
        recruteur.setFormeJuridique(dto.getFormeJuridique());
        recruteur.setNomContact(dto.getNomContact());
        recruteur.setFonction(dto.getFonction());
        recruteur.setTelephone(dto.getTelephone());
        recruteur.setFax(dto.getFax());
        recruteur.setPays(dto.getPays());
        recruteur.setVille(dto.getVille());
        recruteur.setNumeroSiret(dto.getNumeroSiret());
        recruteur.setFichierKbis(dto.getFichierKbis().getBytes()); // Stockage du fichier en tant que byte[]

        recruteur.setStatutValidation("EN_ATTENTE");
        recruteur.setRole(RoleUtilisateur.RECRUTEUR);
        recruteur.setConditionsAcceptees(dto.isConditionsAcceptees());
        recruteur.setEmailVerifie(false);
        recruteur.setCompteActif(false);

        recruteurRepository.save(recruteur);
        logger.info("Recruteur sauvegardé avec succès : {}", recruteur.getEmail());

        envoyerEmailVerification(recruteur.getEmail(), recruteur.getId());
    }

    @Transactional
    public void inscrireAdministrateur(AdministrateurInscriptionDTO dto) throws MessagingException {
        logger.info("Début de l'inscription pour l'administrateur avec email : {}", dto.getEmail());

        validateCommonFields(dto.getEmail(), dto.getMotDePasse(), dto.getEmailConfirmation(), dto.getMotDePasseConfirmation(), dto.getConditionsAcceptees());

        validateRequiredField(dto.getPrenom(), "Le prénom est obligatoire");
        validateRequiredField(dto.getNom(), "Le nom est obligatoire");

        checkEmailExists(dto.getEmail());

        Administrateur administrateur = new Administrateur();
        administrateur.setEmail(dto.getEmail().trim());
        String hashedPassword = passwordEncoder.encode(dto.getMotDePasse());
        administrateur.setMotDePasse(hashedPassword);
        logger.info("Mot de passe haché pour l'administrateur {} : {}", dto.getEmail(), hashedPassword);

        administrateur.setPrenom(dto.getPrenom());
        administrateur.setNom(dto.getNom());
        administrateur.setConditionsAcceptees(dto.getConditionsAcceptees());
        administrateur.setRole(RoleUtilisateur.ADMINISTRATEUR);
        administrateur.setEmailVerifie(false);
        administrateur.setCompteActif(false);

        administrateurRepository.save(administrateur);
        logger.info("Administrateur sauvegardé avec succès : {}", administrateur.getEmail());

        envoyerEmailVerification(administrateur.getEmail(), administrateur.getId());
    }

    @Transactional
    public void verifierEmail(String token) throws MessagingException {
        logger.info("Vérification de l'email avec le token : {}", token);

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    logger.error("Token invalide : {}", token);
                    return new IllegalArgumentException("Token invalide");
                });

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            logger.error("Token expiré : {}", token);
            User user = userRepository.findById(verificationToken.getUserId())
                    .orElseThrow(() -> {
                        logger.error("Utilisateur non trouvé pour le token : {}", token);
                        return new IllegalArgumentException("Utilisateur non trouvé");
                    });
            verificationTokenRepository.delete(verificationToken);
            envoyerEmailVerification(user.getEmail(), user.getId());
            throw new IllegalArgumentException("Token expiré. Un nouvel email de vérification a été envoyé.");
        }

        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé pour le token : {}", token);
                    return new IllegalArgumentException("Utilisateur non trouvé");
                });

        user.setEmailVerifie(true);
        if (user instanceof Candidat || user instanceof Administrateur) {
            user.setCompteActif(true);
        }
        userRepository.save(user);
        logger.info("Email vérifié avec succès pour l'utilisateur : {}", user.getEmail());

        verificationTokenRepository.delete(verificationToken);
        logger.info("Token de vérification supprimé : {}", token);
    }

    private void validateCommonFields(String email, String motDePasse, String emailConfirmation, String motDePasseConfirmation, boolean conditionsAcceptees) {
        EmailValidator.validerEmail(email);
        PasswordValidator.validatePassword(motDePasse);

        if (!email.equals(emailConfirmation)) {
            logger.error("Les emails ne correspondent pas : {}", email);
            throw new IllegalArgumentException("Les emails ne correspondent pas");
        }

        if (!motDePasse.equals(motDePasseConfirmation)) {
            logger.error("Les mots de passe ne correspondent pas pour : {}", email);
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
        }

        if (!conditionsAcceptees) {
            logger.error("Les conditions ne sont pas acceptées pour : {}", email);
            throw new IllegalArgumentException("Vous devez accepter les termes et conditions");
        }
    }

    private void validateRequiredField(String field, String errorMessage) {
        if (field == null || field.trim().isEmpty()) {
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void checkEmailExists(String email) {
        if (userRepository.findByEmail(email.trim()).isPresent()) {
            logger.error("Email déjà utilisé : {}", email);
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }
    }

    private void envoyerEmailVerification(String email, Long userId) throws MessagingException {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);

        VerificationToken verificationToken = new VerificationToken(token, userId, expiryDate);
        verificationTokenRepository.save(verificationToken);

        String lien = "http://localhost:8080/api/inscription/verifier-email?token=" + token;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject("Vérification de votre email - Plateforme de Recrutement");
        String htmlContent = "<h2>Bienvenue sur notre plateforme de recrutement !</h2>"
                + "<p>Merci de vous être inscrit(e). Pour activer votre compte, veuillez cliquer sur le lien ci-dessous :</p>"
                + "<p><a href=\"" + lien + "\">Vérifier mon email</a></p>"
                + "<p>Ce lien est valide pendant 24 heures. Si vous ne l'avez pas demandé, ignorez cet email.</p>"
                + "<p>À bientôt,<br>L'équipe de la plateforme de recrutement</p>";
        helper.setText(htmlContent, true);

        mailSender.send(message);
        logger.info("Email de vérification envoyé avec succès à : {}", email);
    }

    @Transactional(readOnly = true)
    public Candidat getCandidatProfileByEmail(String email) {
        logger.info("Récupération du profil du candidat avec email : {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé avec l'email : {}", email);
                    return new IllegalArgumentException("Utilisateur non trouvé");
                });

        if (!(user instanceof Candidat)) {
            logger.error("L'utilisateur avec l'email {} n'est pas un candidat", email);
            throw new IllegalArgumentException("L'utilisateur n'est pas un candidat");
        }

        Candidat candidat = (Candidat) user;

        if (candidat.getRole() != RoleUtilisateur.CANDIDAT) {
            logger.error("L'utilisateur {} n'est pas un candidat", email);
            throw new IllegalArgumentException("Seul un candidat peut accéder à son profil");
        }

        logger.info("Profil du candidat récupéré avec succès : email {}", email);
        return candidat;
    }

    @Transactional
    public Candidat updateCandidatProfile(Long id, CandidatUpdateDTO dto, MultipartFile cvFile) throws IOException {
        logger.info("Mise à jour du profil du candidat avec ID : {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé avec l'ID : {}", id);
                    return new IllegalArgumentException("Utilisateur non trouvé");
                });

        if (!(user instanceof Candidat)) {
            logger.error("L'utilisateur avec l'ID {} n'est pas un candidat", id);
            throw new IllegalArgumentException("L'utilisateur n'est pas un candidat");
        }

        Candidat candidat = (Candidat) user;
        updateCandidatFromDTO(candidat, dto, cvFile);

        Candidat updatedCandidat = (Candidat) userRepository.save(candidat);
        logger.info("Profil du candidat mis à jour avec succès : ID {}", updatedCandidat.getId());
        return updatedCandidat;
    }

    @Transactional
    public Candidat updateCandidatProfileByEmail(String email, CandidatUpdateDTO dto, MultipartFile cvFile) throws IOException {
        logger.info("Mise à jour du profil du candidat avec email : {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé avec l'email : {}", email);
                    return new IllegalArgumentException("Utilisateur non trouvé");
                });

        if (!(user instanceof Candidat)) {
            logger.error("L'utilisateur avec l'email {} n'est pas un candidat", email);
            throw new IllegalArgumentException("L'utilisateur n'est pas un candidat");
        }

        Candidat candidat = (Candidat) user;

        if (candidat.getRole() != RoleUtilisateur.CANDIDAT) {
            logger.error("L'utilisateur {} n'est pas un candidat", email);
            throw new IllegalArgumentException("Seul un candidat peut mettre à jour son profil");
        }

        updateCandidatFromDTO(candidat, dto, cvFile);

        Candidat updatedCandidat = (Candidat) userRepository.save(candidat);
        logger.info("Profil du candidat mis à jour avec succès : email {}", email);
        return updatedCandidat;
    }

    private void updateCandidatFromDTO(Candidat candidat, CandidatUpdateDTO dto, MultipartFile cvFile) throws IOException {
        // Gestion du fichier CV
        if (cvFile != null && !cvFile.isEmpty()) {
            logger.debug("Vérification du fichier CV : contentType = {}", cvFile.getContentType());
            // Vérifier que le fichier est un PDF
            if (!cvFile.getContentType().equals("application/pdf")) {
                logger.error("Le fichier CV doit être un PDF");
                throw new IllegalArgumentException("Le fichier CV doit être un PDF");
            }
            // Vérifier la taille du fichier (max 5MB)
            long maxFileSize = 5 * 1024 * 1024; // 5MB
            if (cvFile.getSize() > maxFileSize) {
                logger.error("Le fichier CV est trop volumineux (max 5MB)");
                throw new IllegalArgumentException("Le fichier CV est trop volumineux (max 5MB)");
            }
            logger.info("Mise à jour du CV pour le candidat : {} octets", cvFile.getSize());
            candidat.setCv(cvFile.getBytes());
        } else {
            logger.info("Aucun fichier CV fourni, le CV existant est conservé");
        }

        // Mise à jour des champs uniquement si fournis
        if (dto.getCivilite() != null) {
            candidat.setCivilite(dto.getCivilite());
        }
        if (dto.getPrenom() != null) {
            candidat.setPrenom(dto.getPrenom());
        }
        if (dto.getNom() != null) {
            candidat.setNom(dto.getNom());
        }
        if (dto.getTelephone() != null) {
            candidat.setTelephone(dto.getTelephone());
        }
        if (dto.getCompetences() != null) {
            candidat.setCompetences(dto.getCompetences());
        }
        if (dto.getLangues() != null) {
            candidat.setLangues(dto.getLangues());
        }
        if (dto.getDateNaissance() != null) {
            candidat.setDateNaissance(dto.getDateNaissance());
        }
        if (dto.getPays() != null) {
            candidat.setPays(dto.getPays());
        }
        if (dto.getVille() != null) {
            candidat.setVille(dto.getVille());
        }
        if (dto.getDomaineEtudes() != null) {
            candidat.setDomaineEtudes(dto.getDomaineEtudes());
        }
        if (dto.getTypeFormation() != null) {
            candidat.setTypeFormation(dto.getTypeFormation());
        }
        if (dto.getNiveauEtudes() != null) {
            candidat.setNiveauEtudes(dto.getNiveauEtudes());
        }

        // Gestion des listes (certifications, formations, expériences, projets)
        if (dto.getCertifications() != null) {
            List<Certification> certifications = dto.getCertifications().stream().map(str -> {
                Certification certification = new Certification();
                certification.setNom(str);
                return certification;
            }).collect(Collectors.toList());
            candidat.setCertifications(certifications);
        }
        if (dto.getFormations() != null) {
            List<Formation> formations = dto.getFormations().stream().map(str -> {
                Formation formation = new Formation();
                formation.setNom(str);
                return formation;
            }).collect(Collectors.toList());
            candidat.setFormations(formations);
        }
        if (dto.getExperiences() != null) {
            List<Experience> experiences = dto.getExperiences().stream().map(str -> {
                Experience experience = new Experience();
                experience.setTitre(str);
                return experience;
            }).collect(Collectors.toList());
            candidat.setExperiences(experiences);
        }
        if (dto.getProjets() != null) {
            List<Projet> projets = dto.getProjets().stream().map(str -> {
                Projet projet = new Projet();
                projet.setTitre(str);
                return projet;
            }).collect(Collectors.toList());
            candidat.setProjets(projets);
        }
    }
}