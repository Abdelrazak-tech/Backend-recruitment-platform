package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "candidatures")
public class Candidature {

    public enum StatutCandidature {
        EN_ATTENTE, ACCEPTEE, REJETEE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidat_id", nullable = false)
    private Candidat candidat;

    @ManyToOne
    @JoinColumn(name = "offre_emploi_id", nullable = false)
    private OffreEmploi offreEmploi;

    @Column(name = "date_candidature", nullable = false)
    private LocalDateTime dateCandidature;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutCandidature statut;

    @Lob
    @Column(name = "cv")
    private byte[] cv; // Nouveau champ pour stocker le CV

    // Constructeurs
    public Candidature() {
        this.dateCandidature = LocalDateTime.now();
        this.statut = StatutCandidature.EN_ATTENTE;
    }

    public Candidature(Candidat candidat, OffreEmploi offreEmploi, byte[] cv) {
        this.candidat = candidat;
        this.offreEmploi = offreEmploi;
        this.dateCandidature = LocalDateTime.now();
        this.statut = StatutCandidature.EN_ATTENTE;
        this.cv = cv;
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Candidat getCandidat() { return candidat; }
    public void setCandidat(Candidat candidat) { this.candidat = candidat; }
    public OffreEmploi getOffreEmploi() { return offreEmploi; }
    public void setOffreEmploi(OffreEmploi offreEmploi) { this.offreEmploi = offreEmploi; }
    public LocalDateTime getDateCandidature() { return dateCandidature; }
    public void setDateCandidature(LocalDateTime dateCandidature) { this.dateCandidature = dateCandidature; }
    public StatutCandidature getStatut() { return statut; }
    public void setStatut(StatutCandidature statut) { this.statut = statut; }
    public byte[] getCv() { return cv; }
    public void setCv(byte[] cv) { this.cv = cv; }
}