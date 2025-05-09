package com.example.demo.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob; // Ajout pour gérer les données binaires volumineuses
import jakarta.persistence.OneToMany;

@Entity
@DiscriminatorValue("CANDIDAT")
public class Candidat extends User {

    @Column
    private String civilite;

    @Column
    private String prenom;

    @Column
    private String nom;

    @Column
    private String telephone;

    @Lob // Annotation pour indiquer que ce champ peut contenir une grande quantité de données (comme un fichier PDF)
    @Column(name = "cv")
    private byte[] cv; // Remplacement de cheminCv par cv de type byte[]

    @ElementCollection
    @CollectionTable(name = "candidat_competences", joinColumns = @JoinColumn(name = "candidat_id"))
    @Column(name = "competence")
    private List<String> competences;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "candidat_id")
    private List<Certification> certifications;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "candidat_id")
    private List<Formation> formations;

    @ElementCollection
    @CollectionTable(name = "candidat_langues", joinColumns = @JoinColumn(name = "candidat_id"))
    @Column(name = "langue")
    private List<String> langues;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "candidat_id")
    private List<Experience> experiences;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "candidat_id")
    private List<Projet> projets;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column
    private String pays;

    @Column
    private String ville;

    @Column(name = "domaine_etudes")
    private String domaineEtudes;

    @Column(name = "type_formation")
    private String typeFormation;

    @Column(name = "niveau_etudes")
    private String niveauEtudes;

    // Constructeurs
    public Candidat() {
        setRole(RoleUtilisateur.CANDIDAT);
    }

    // Getters et setters
    public String getCivilite() { return civilite; }
    public void setCivilite(String civilite) { this.civilite = civilite; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public byte[] getCv() { return cv; } // Remplacement de getCheminCv
    public void setCv(byte[] cv) { this.cv = cv; } // Remplacement de setCheminCv
    public List<String> getCompetences() { return competences; }
    public void setCompetences(List<String> competences) { this.competences = competences; }
    public List<Certification> getCertifications() { return certifications; }
    public void setCertifications(List<Certification> certifications) { this.certifications = certifications; }
    public List<Formation> getFormations() { return formations; }
    public void setFormations(List<Formation> formations) { this.formations = formations; }
    public List<String> getLangues() { return langues; }
    public void setLangues(List<String> langues) { this.langues = langues; }
    public List<Experience> getExperiences() { return experiences; }
    public void setExperiences(List<Experience> experiences) { this.experiences = experiences; }
    public List<Projet> getProjets() { return projets; }
    public void setProjets(List<Projet> projets) { this.projets = projets; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getDomaineEtudes() { return domaineEtudes; }
    public void setDomaineEtudes(String domaineEtudes) { this.domaineEtudes = domaineEtudes; }
    public String getTypeFormation() { return typeFormation; }
    public void setTypeFormation(String typeFormation) { this.typeFormation = typeFormation; }
    public String getNiveauEtudes() { return niveauEtudes; }
    public void setNiveauEtudes(String niveauEtudes) { this.niveauEtudes = niveauEtudes; }
}