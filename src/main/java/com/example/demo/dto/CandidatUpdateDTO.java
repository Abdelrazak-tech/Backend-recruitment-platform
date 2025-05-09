package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class CandidatUpdateDTO {

    private String civilite;

    private String prenom;

    private String nom;

    private String telephone;

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

    private MultipartFile cvFile;

    // Getters et setters
    public String getCivilite() { return civilite; }
    public void setCivilite(String civilite) { this.civilite = civilite; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public List<String> getCompetences() { return competences; }
    public void setCompetences(List<String> competences) { this.competences = competences; }
    public List<String> getCertifications() { return certifications; }
    public void setCertifications(List<String> certifications) { this.certifications = certifications; }
    public List<String> getFormations() { return formations; }
    public void setFormations(List<String> formations) { this.formations = formations; }
    public List<String> getLangues() { return langues; }
    public void setLangues(List<String> langues) { this.langues = langues; }
    public List<String> getExperiences() { return experiences; }
    public void setExperiences(List<String> experiences) { this.experiences = experiences; }
    public List<String> getProjets() { return projets; }
    public void setProjets(List<String> projets) { this.projets = projets; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getDomaineEtudes() { return domaineEtudes; }
    public void setDomaineEtudes(String domaineEtudes) { this.domaineEtudes = domaineEtudes; }
    public String getTypeFormation() { return typeFormation; }
    public void setTypeFormation(String typeFormation) { this.typeFormation = typeFormation; }
    public String getNiveauEtudes() { return niveauEtudes; }
    public void setNiveauEtudes(String niveauEtudes) { this.niveauEtudes = niveauEtudes; }
    public MultipartFile getCvFile() { return cvFile; }
    public void setCvFile(MultipartFile cvFile) { this.cvFile = cvFile; }
}