package com.example.demo.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMINISTRATEUR")
public class Administrateur extends User {

    private String prenom;
    private String nom;

    // Constructeurs
    public Administrateur() {
        setRole(RoleUtilisateur.ADMINISTRATEUR);
        setConditionsAcceptees(true); // Les administrateurs n'ont pas besoin de cocher les conditions
        setCompteActif(true); // Les administrateurs sont actifs par défaut
    }

    // Getters et setters
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}