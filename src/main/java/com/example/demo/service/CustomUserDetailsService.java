package com.example.demo.service;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Tentative de chargement de l'utilisateur avec email : {}", email);

        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> {
                    logger.error("Utilisateur introuvable avec l'email : {}", email);
                    return new UsernameNotFoundException("Utilisateur introuvable avec l'email : " + email);
                });

        if (!user.isEmailVerifie()) {
            logger.warn("Email non vérifié pour l'utilisateur : {}", email);
            throw new UsernameNotFoundException("L'email n'est pas vérifié pour l'utilisateur : " + email);
        }

        if (!user.isCompteActif()) {
            logger.warn("Compte inactif pour l'utilisateur : {}", email);
            throw new UsernameNotFoundException("Compte inactif ou en attente de validation pour l'utilisateur : " + email);
        }

        String role = user.getRole() != null ? user.getRole().name() : null;
        if (role == null || role.trim().isEmpty()) {
            logger.error("Rôle non défini pour l'utilisateur : {}", email);
            throw new UsernameNotFoundException("Rôle non défini pour l'utilisateur : " + email);
        }

        logger.info("Utilisateur chargé avec succès : {}, rôle : {}", email, role);
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getMotDePasse(),
                Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(role))
        );
    }
}