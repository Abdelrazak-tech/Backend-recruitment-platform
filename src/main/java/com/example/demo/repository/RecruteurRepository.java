package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Recruteur;

public interface RecruteurRepository extends JpaRepository<Recruteur, Long> {
    Optional<Recruteur> findByEmail(String email);
    List<Recruteur> findByStatutValidation(String statutValidation);
}