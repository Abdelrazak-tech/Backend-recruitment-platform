package com.example.demo.dto;

import org.springframework.web.multipart.MultipartFile;

public class DemandeAdminDTO {

    private String nom;

    private String email;

    private String sujet;

    private String message;

    private MultipartFile pieceJointe;

    // Getters et setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public MultipartFile getPieceJointe() { return pieceJointe; }
    public void setPieceJointe(MultipartFile pieceJointe) { this.pieceJointe = pieceJointe; }
}