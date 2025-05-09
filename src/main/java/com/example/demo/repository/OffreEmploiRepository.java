package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.OffreEmploi;
import com.example.demo.entity.OffreEmploi.StatutOffre;
import com.example.demo.entity.User;

@Repository
public interface OffreEmploiRepository extends JpaRepository<OffreEmploi, Long> {

    List<OffreEmploi> findByUser(User user);

    List<OffreEmploi> findByStatut(StatutOffre statut);

    List<OffreEmploi> findByUserAndStatut(User user, StatutOffre statut);
}