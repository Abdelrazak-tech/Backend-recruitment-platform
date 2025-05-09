package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Notification;
import com.example.demo.entity.User;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDestinataireAndLuFalse(User destinataire); // Récupérer les notifications non lues
    List<Notification> findByDestinataire(User destinataire); // Récupérer toutes les notifications d’un utilisateur
}