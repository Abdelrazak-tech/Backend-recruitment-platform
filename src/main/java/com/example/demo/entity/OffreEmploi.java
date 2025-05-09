package com.example.demo.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "offres_emploi")
public class OffreEmploi {

    public enum StatutOffre {
        ACTIVE, EXPIREE, FERMEE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private String entreprise;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String localisation;

    @Column(name = "type_contrat", nullable = false)
    private String typeContrat;

    @Column(name = "date_creation", nullable = false)
    private LocalDate dateCreation;

    @Column(name = "date_expiration", nullable = false)
    private LocalDate dateExpiration;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column
    private String salaire;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutOffre statut;

    @Column
    private List<String> missions;

    @Column(name = "profil_recherche", nullable = false)
    private List<String> profilRecherche;

    @Column
    private List<String> avantages;

    @Column
    private String contact;

    @Column(name = "informations_entreprise")
    private String informationsEntreprise;

    @Column(name = "processus_recrutement")
    private String processusRecrutement;

    @Column(name = "notification_envoyee")
    private boolean notificationEnvoyee;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Ajout de la relation avec les candidatures
    @OneToMany(mappedBy = "offreEmploi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidature> candidatures;

    // Constructeurs
    public OffreEmploi() {
        this.notificationEnvoyee = false;
        this.statut = StatutOffre.ACTIVE;
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getEntreprise() { return entreprise; }
    public void setEntreprise(String entreprise) { this.entreprise = entreprise; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public String getTypeContrat() { return typeContrat; }
    public void setTypeContrat(String typeContrat) { this.typeContrat = typeContrat; }
    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }
    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public String getSalaire() { return salaire; }
    public void setSalaire(String salaire) { this.salaire = salaire; }
    public StatutOffre getStatut() { return statut; }
    public void setStatut(StatutOffre statut) { this.statut = statut; }
    public List<String> getMissions() { return missions; }
    public void setMissions(List<String> missions) { this.missions = missions; }
    public List<String> getProfilRecherche() { return profilRecherche; }
    public void setProfilRecherche(List<String> profilRecherche) { this.profilRecherche = profilRecherche; }
    public List<String> getAvantages() { return avantages; }
    public void setAvantages(List<String> avantages) { this.avantages = avantages; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getInformationsEntreprise() { return informationsEntreprise; }
    public void setInformationsEntreprise(String informationsEntreprise) { this.informationsEntreprise = informationsEntreprise; }
    public String getProcessusRecrutement() { return processusRecrutement; }
    public void setProcessusRecrutement(String processusRecrutement) { this.processusRecrutement = processusRecrutement; }
    public boolean isNotificationEnvoyee() { return notificationEnvoyee; }
    public void setNotificationEnvoyee(boolean notificationEnvoyee) { this.notificationEnvoyee = notificationEnvoyee; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Candidature> getCandidatures() { return candidatures; }
    public void setCandidatures(List<Candidature> candidatures) { this.candidatures = candidatures; }
}