package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.DemandeOffre;
import com.example.demo.entity.OffreEmploi;

public interface DemandeOffreRepository extends JpaRepository<DemandeOffre, Long> {
    List<DemandeOffre> findByOffreEmploiIn(List<OffreEmploi> offreEmplois);
}