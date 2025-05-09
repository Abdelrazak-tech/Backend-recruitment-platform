package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "demandes_admin")
public class DemandeAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String nom;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String sujet;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Lob
    @Column(name = "piece_jointe", columnDefinition = "bytea", nullable = true) // Optionnel
    private byte[] pieceJointe;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateSoumission;

    // Constructeur
    public DemandeAdmin() {
        this.dateSoumission = LocalDateTime.now();
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public byte[] getPieceJointe() { return pieceJointe; }
    public void setPieceJointe(byte[] pieceJointe) { this.pieceJointe = pieceJointe; }
    public LocalDateTime getDateSoumission() { return dateSoumission; }
    public void setDateSoumission(LocalDateTime dateSoumission) { this.dateSoumission = dateSoumission; }
}