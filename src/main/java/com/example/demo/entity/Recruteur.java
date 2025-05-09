package com.example.demo.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;

@Entity
@DiscriminatorValue("RECRUTEUR")
public class Recruteur extends User {

    private String raisonSociale;
    private String formeJuridique;
    private String nomContact;
    private String fonction;
    private String telephone;
    private String fax;
    private String pays;
    private String ville;
    private String numeroSiret;

    @Lob
    @org.hibernate.annotations.LazyGroup("lobs")
    @org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SELECT)
    private byte[] fichierKbis;

    private String statutValidation;

    // Constructeurs
    public Recruteur() {
        setRole(RoleUtilisateur.RECRUTEUR);
    }

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
    public String getNumeroSiret() { return numeroSiret; }
    public void setNumeroSiret(String numeroSiret) { this.numeroSiret = numeroSiret; }
    public byte[] getFichierKbis() { return fichierKbis; }
    public void setFichierKbis(byte[] fichierKbis) { this.fichierKbis = fichierKbis; }
    public String getStatutValidation() { return statutValidation; }
    public void setStatutValidation(String statutValidation) { this.statutValidation = statutValidation; }
}