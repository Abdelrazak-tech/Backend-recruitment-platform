package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AdministrateurInscriptionDTO {

    @NotBlank(message = "Le prénom est requis")
    private String prenom;

    @NotBlank(message = "Le nom est requis")
    private String nom;

    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "La confirmation de l'email est requise")
    private String emailConfirmation;

    @NotBlank(message = "Le mot de passe est requis")
    private String motDePasse;

    @NotBlank(message = "La confirmation du mot de passe est requise")
    private String motDePasseConfirmation;

    @NotNull(message = "Les conditions doivent être acceptées")
    private Boolean conditionsAcceptees;

    // Getters et setters
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEmailConfirmation() { return emailConfirmation; }
    public void setEmailConfirmation(String emailConfirmation) { this.emailConfirmation = emailConfirmation; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getMotDePasseConfirmation() { return motDePasseConfirmation; }
    public void setMotDePasseConfirmation(String motDePasseConfirmation) { this.motDePasseConfirmation = motDePasseConfirmation; }
    public Boolean getConditionsAcceptees() { return conditionsAcceptees; }
    public void setConditionsAcceptees(Boolean conditionsAcceptees) { this.conditionsAcceptees = conditionsAcceptees; }
}