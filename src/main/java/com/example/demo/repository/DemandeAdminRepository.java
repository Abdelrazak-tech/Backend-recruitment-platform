package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.DemandeAdmin;

public interface DemandeAdminRepository extends JpaRepository<DemandeAdmin, Long> {
}