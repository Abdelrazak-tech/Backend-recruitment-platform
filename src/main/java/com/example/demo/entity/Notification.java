package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User destinataire; // Le candidat qui reçoit la notification

    private String message; // Exemple : "Nouvelle offre : Ingénieur DevOps chez TechCorp"

    private String lien; // Lien vers l'offre (exemple : "/offres/1")

    private LocalDateTime dateCreation;

    private boolean lu; // Indique si la notification a été lue

    // Constructeurs
    public Notification() {
        this.dateCreation = LocalDateTime.now();
        this.lu = false;
    }

    public Notification(User destinataire, String message, String lien) {
        this.destinataire = destinataire;
        this.message = message;
        this.lien = lien;
        this.dateCreation = LocalDateTime.now();
        this.lu = false;
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getDestinataire() { return destinataire; }
    public void setDestinataire(User destinataire) { this.destinataire = destinataire; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getLien() { return lien; }
    public void setLien(String lien) { this.lien = lien; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }
}