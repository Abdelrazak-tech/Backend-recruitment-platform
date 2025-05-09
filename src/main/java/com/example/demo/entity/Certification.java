package com.example.demo.entity;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "certification")
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(name = "institution")
    private String institution;

    @Column(name = "chemin_fichier")
    private String cheminFichier;

    @ElementCollection
    @CollectionTable(name = "certification_competences", joinColumns = @JoinColumn(name = "certification_id"))
    @Column(name = "competence")
    private List<String> competencesAcquises;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
    public String getCheminFichier() { return cheminFichier; }
    public void setCheminFichier(String cheminFichier) { this.cheminFichier = cheminFichier; }
    public List<String> getCompetencesAcquises() { return competencesAcquises; }
    public void setCompetencesAcquises(List<String> competencesAcquises) { this.competencesAcquises = competencesAcquises; }
}