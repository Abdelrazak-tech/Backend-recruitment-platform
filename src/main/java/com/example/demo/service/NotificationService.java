package com.example.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.repository.NotificationRepository;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void creerNotification(User destinataire, String message, String lien) {
        Notification notification = new Notification(destinataire, message, lien);
        notificationRepository.save(notification);
        logger.info("Notification créée pour l'utilisateur {} : {}", destinataire.getEmail(), message);
    }

    @Transactional
    public List<Notification> getNotificationsNonLues(User user) {
        return notificationRepository.findByDestinataireAndLuFalse(user);
    }

    @Transactional
    public List<Notification> getToutesNotifications(User user) {
        return notificationRepository.findByDestinataire(user);
    }

    @Transactional
    public void marquerCommeLu(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification non trouvée"));
        notification.setLu(true);
        notificationRepository.save(notification);
        logger.info("Notification marquée comme lue : ID {}", notificationId);
    }
}