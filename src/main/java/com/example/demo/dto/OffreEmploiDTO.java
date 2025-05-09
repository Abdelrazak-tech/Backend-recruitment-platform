package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.demo.entity.OffreEmploi.StatutOffre;

public class OffreEmploiDTO {

    private String titre;
    private String entreprise;
    private String description;
    private String localisation;
    private String typeContrat;
    private LocalDate dateCreation;
    private LocalDate dateExpiration;
    private LocalDate dateDebut;
    private String salaire;
    private StatutOffre statut;
    private boolean notificationEnvoyee;
    private List<String> missions;
    private List<String> profilRecherche;
    private List<String> avantages;
    private String contact;
    private String informationsEntreprise; // Nouveau champ
    private String processusRecrutement; // Nouveau champ
    private Long userId;
    private boolean prolonger; // Utilisé pour la méthode prolongerOffreEmploi

    // Constructeurs
    public OffreEmploiDTO() {
    }

    // Getters et setters
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
    public boolean isNotificationEnvoyee() { return notificationEnvoyee; }
    public void setNotificationEnvoyee(boolean notificationEnvoyee) { this.notificationEnvoyee = notificationEnvoyee; }
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
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public boolean isProlonger() { return prolonger; }
    public void setProlonger(boolean prolonger) { this.prolonger = prolonger; }
}