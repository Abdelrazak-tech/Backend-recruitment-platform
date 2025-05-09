package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Candidat;
import com.example.demo.entity.Candidature;
import com.example.demo.entity.OffreEmploi;

@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, Long> {

    // Vérifier si une candidature existe déjà pour ce candidat et cette offre
    boolean existsByCandidatAndOffreEmploi(Candidat candidat, OffreEmploi offreEmploi);

    // Récupérer toutes les candidatures pour une offre donnée
    List<Candidature> findByOffreEmploi(OffreEmploi offreEmploi);

    // Récupérer toutes les candidatures d’un candidat
    List<Candidature> findByCandidat(Candidat candidat);
}