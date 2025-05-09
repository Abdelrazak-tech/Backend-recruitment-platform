package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping; // Ajouté pour supporter GET
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @PostMapping("/admin")
    @GetMapping("/admin") // Supporte également GET pour aligner avec la redirection
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public String adminDashboard() {
        return "Bienvenue sur le tableau de bord administrateur!";
    }

    @PostMapping("/recruteur")
    @GetMapping("/recruteur")
    @PreAuthorize("hasRole('RECRUTEUR')")
    public String recruteurDashboard() {
        return "Bienvenue sur le tableau de bord recruteur!";
    }

    @PostMapping("/candidat")
    @GetMapping("/candidat")
    @PreAuthorize("hasRole('CANDIDAT')")
    public String candidatDashboard() {
        return "Bienvenue sur le tableau de bord candidat!";
    }
}