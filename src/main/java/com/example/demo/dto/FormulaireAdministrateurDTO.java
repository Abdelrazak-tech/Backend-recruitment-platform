package com.example.demo.dto;

public class FormulaireAdministrateurDTO {

    private String nomContact; // Nom complet de l’expéditeur
    private String emailContact; // Email de l’expéditeur
    private String typeFormulaire; // "PLATEFORME" ou "INTEGRATION_ENTREPRISE" (type de demande)
    private String message; // Message principal envoyé par l’expéditeur
    private String telephoneContact; // Téléphone de l’expéditeur (optionnel)

    // Constructeurs
    public FormulaireAdministrateurDTO() {
    }

    // Getters et setters
    public String getNomContact() { return nomContact; }
    public void setNomContact(String nomContact) { this.nomContact = nomContact; }
    public String getEmailContact() { return emailContact; }
    public void setEmailContact(String emailContact) { this.emailContact = emailContact; }
    public String getTypeFormulaire() { return typeFormulaire; }
    public void setTypeFormulaire(String typeFormulaire) { this.typeFormulaire = typeFormulaire; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getTelephoneContact() { return telephoneContact; }
    public void setTelephoneContact(String telephoneContact) { this.telephoneContact = telephoneContact; }
}