package com.example.demo.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RecruteurInscriptionDTO {

    @NotBlank(message = "La raison sociale est requise")
    private String raisonSociale;

    @NotBlank(message = "La forme juridique est requise")
    private String formeJuridique;

    @NotBlank(message = "Le nom du contact est requis")
    private String nomContact;

    @NotBlank(message = "La fonction est requise")
    private String fonction;

    @NotBlank(message = "Le téléphone est requis")
    private String telephone;

    private String fax;

    @NotBlank(message = "Le pays est requis")
    private String pays;

    @NotBlank(message = "La ville est requise")
    private String ville;

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

    @NotBlank(message = "Le numéro SIRET est requis")
    private String numeroSiret;

    private MultipartFile fichierKbis;

    // Getters et setters
    public String getRaisonSociale() { return raisonSociale; }
    public void setRaisonSociale(String raisonSociale) { this.raisonSociale = raisonSociale; }
    public String getFormeJuridique() { return formeJuridique; }
    public void setFormeJuridique(String formeJuridique) { this.formeJuridique = formeJuridique; }
    public String getNomContact() { return nomContact; }
    public void setNomContact(String nomContact) { this.nomContact = nomContact; }
    public String getFonction() { return fonction; }
    public void setFonction(String fonction) { this.fonction = fonction; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEmailConfirmation() { return emailConfirmation; }
    public void setEmailConfirmation(String emailConfirmation) { this.emailConfirmation = emailConfirmation; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getMotDePasseConfirmation() { return motDePasseConfirmation; }
    public void setMotDePasseConfirmation(String motDePasseConfirmation) { this.motDePasseConfirmation = motDePasseConfirmation; }
    public Boolean isConditionsAcceptees() { return conditionsAcceptees; }
    public void setConditionsAcceptees(Boolean conditionsAcceptees) { this.conditionsAcceptees = conditionsAcceptees; }
    public String getNumeroSiret() { return numeroSiret; }
    public void setNumeroSiret(String numeroSiret) { this.numeroSiret = numeroSiret; }
    public MultipartFile getFichierKbis() { return fichierKbis; }
    public void setFichierKbis(MultipartFile fichierKbis) { this.fichierKbis = fichierKbis; }
}