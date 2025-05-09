package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "demandes_offres")
public class DemandeOffre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offre_emploi_id", nullable = false)
    private OffreEmploi offreEmploi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidat_id", nullable = false)
    private Candidat candidat;

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
    public DemandeOffre() {
        this.dateSoumission = LocalDateTime.now();
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public OffreEmploi getOffreEmploi() { return offreEmploi; }
    public void setOffreEmploi(OffreEmploi offreEmploi) { this.offreEmploi = offreEmploi; }
    public Candidat getCandidat() { return candidat; }
    public void setCandidat(Candidat candidat) { this.candidat = candidat; }
    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public byte[] getPieceJointe() { return pieceJointe; }
    public void setPieceJointe(byte[] pieceJointe) { this.pieceJointe = pieceJointe; }
    public LocalDateTime getDateSoumission() { return dateSoumission; }
    public void setDateSoumission(LocalDateTime dateSoumission) { this.dateSoumission = dateSoumission; }
}