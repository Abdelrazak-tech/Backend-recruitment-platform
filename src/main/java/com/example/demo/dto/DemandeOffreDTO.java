package com.example.demo.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;

public class DemandeOffreDTO {

    @NotBlank(message = "Le nom du candidat est requis")
    private String nomCandidat;

    @NotBlank(message = "Le sujet est requis")
    private String sujet;

    @NotBlank(message = "Le message est requis")
    private String message;

    private MultipartFile pieceJointe;

    // Getters et setters
    public String getNomCandidat() { return nomCandidat; }
    public void setNomCandidat(String nomCandidat) { this.nomCandidat = nomCandidat; }
    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public MultipartFile getPieceJointe() { return pieceJointe; }
    public void setPieceJointe(MultipartFile pieceJointe) { this.pieceJointe = pieceJointe; }
}