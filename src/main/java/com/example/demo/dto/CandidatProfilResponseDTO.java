package com.example.demo.dto;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.entity.Candidat;

public class CandidatProfilResponseDTO {

    private String civilite;
    private String prenom;
    private String nom;
    private String email;
    private String telephone;
    private String cv;
    private List<String> competences;
    private List<String> certifications;
    private List<String> formations;
    private List<String> langues;
    private List<String> experiences;
    private List<String> projets;
    private LocalDate dateNaissance;
    private String pays;
    private String ville;
    private String domaineEtudes;
    private String typeFormation;
    private String niveauEtudes;

    public CandidatProfilResponseDTO(Candidat candidat) {
        this.civilite = candidat.getCivilite();
        this.prenom = candidat.getPrenom();
        this.nom = candidat.getNom();
        this.email = candidat.getEmail();
        this.telephone = candidat.getTelephone();
        this.cv = candidat.getCv() != null ? Base64.getEncoder().encodeToString(candidat.getCv()) : null;
        this.competences = candidat.getCompetences();
        this.certifications = candidat.getCertifications() != null ? 
            candidat.getCertifications().stream().map(Object::toString).collect(Collectors.toList()) : null;
        this.formations = candidat.getFormations() != null ? 
            candidat.getFormations().stream().map(Object::toString).collect(Collectors.toList()) : null;
        this.langues = candidat.getLangues();
        this.experiences = candidat.getExperiences() != null ? 
            candidat.getExperiences().stream().map(Object::toString).collect(Collectors.toList()) : null;
        this.projets = candidat.getProjets() != null ? 
            candidat.getProjets().stream().map(Object::toString).collect(Collectors.toList()) : null;
        this.dateNaissance = candidat.getDateNaissance();
        this.pays = candidat.getPays();
        this.ville = candidat.getVille();
        this.domaineEtudes = candidat.getDomaineEtudes();
        this.typeFormation = candidat.getTypeFormation();
        this.niveauEtudes = candidat.getNiveauEtudes();
    }

    public String getCivilite() {
        return civilite;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getCv() {
        return cv;
    }

    public List<String> getCompetences() {
        return competences;
    }

    public List<String> getCertifications() {
        return certifications;
    }

    public List<String> getFormations() {
        return formations;
    }

    public List<String> getLangues() {
        return langues;
    }

    public List<String> getExperiences() {
        return experiences;
    }

    public List<String> getProjets() {
        return projets;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public String getPays() {
        return pays;
    }

    public String getVille() {
        return ville;
    }

    public String getDomaineEtudes() {
        return domaineEtudes;
    }

    public String getTypeFormation() {
        return typeFormation;
    }

    public String getNiveauEtudes() {
        return niveauEtudes;
    }
}