package com.example.demo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CandidatInscriptionDTO {

    @NotBlank(message = "La civilité est requise")
    private String civilite;

    @NotBlank(message = "Le prénom est requis")
    private String prenom;

    @NotBlank(message = "Le nom est requis")
    private String nom;

    @NotNull(message = "La date de naissance est requise")
    private LocalDate dateNaissance;

    @NotBlank(message = "Le téléphone est requis")
    private String telephone;

    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "La confirmation de l'email est requise")
    private String emailConfirmation;

    @NotBlank(message = "Le mot de passe est requis")
    private String motDePasse;

    @NotBlank(message = "La confirmation du mot de passe est requise")
    private String motDePasseConfirmation;

    @NotBlank(message = "Le pays est requis")
    private String pays;

    @NotBlank(message = "La ville est requise")
    private String ville;

    @NotBlank(message = "Le domaine d'études est requis")
    private String domaineEtudes;

    @NotBlank(message = "Le type de formation est requis")
    private String typeFormation;

    @NotBlank(message = "Le niveau d'études est requis")
    private String niveauEtudes;

    @NotNull(message = "Les conditions doivent être acceptées")
    private Boolean conditionsAcceptees;

    // Getters et setters
    public String getCivilite() { return civilite; }
    public void setCivilite(String civilite) { this.civilite = civilite; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEmailConfirmation() { return emailConfirmation; }
    public void setEmailConfirmation(String emailConfirmation) { this.emailConfirmation = emailConfirmation; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getMotDePasseConfirmation() { return motDePasseConfirmation; }
    public void setMotDePasseConfirmation(String motDePasseConfirmation) { this.motDePasseConfirmation = motDePasseConfirmation; }
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
    public Boolean getConditionsAcceptees() { return conditionsAcceptees; }
    public void setConditionsAcceptees(Boolean conditionsAcceptees) { this.conditionsAcceptees = conditionsAcceptees; }
}