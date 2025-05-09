package com.example.demo.dto;

public class FormulaireOffreContactDTO {

    private String typeFormulaire; // "OFFRE" ou "ENTREPRISE" (type de question)
    private String message; // Message principal envoyé par le candidat
    private String telephoneContact; // Téléphone du candidat (optionnel)
    private Long offreId; // ID de l’offre (requis, récupéré automatiquement depuis la page de l’offre)

    // Constructeurs
    public FormulaireOffreContactDTO() {
    }

    // Getters et setters
    public String getTypeFormulaire() { return typeFormulaire; }
    public void setTypeFormulaire(String typeFormulaire) { this.typeFormulaire = typeFormulaire; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getTelephoneContact() { return telephoneContact; }
    public void setTelephoneContact(String telephoneContact) { this.telephoneContact = telephoneContact; }
    public Long getOffreId() { return offreId; }
    public void setOffreId(Long offreId) { this.offreId = offreId; }
}