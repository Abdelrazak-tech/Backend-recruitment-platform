package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Util.JwtUtil;
import com.example.demo.dto.LoginRequestDTO;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        try {
            logger.info("Tentative de connexion pour l'email : {}", loginRequest.getEmail());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail().trim(),
                            loginRequest.getMotDePasse()
                    )
            );

            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(authority -> authority.getAuthority())
                    .orElseThrow(() -> {
                        logger.error("Aucun rôle trouvé pour l'utilisateur : {}", loginRequest.getEmail());
                        return new IllegalStateException("Aucun rôle trouvé pour l'utilisateur");
                    });

            String token = jwtUtil.generateToken(loginRequest.getEmail(), role);

            Cookie jwtCookie = new Cookie("jwt_token", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false); // À activer en production avec HTTPS
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge((int) (jwtUtil.getJwtTokenValidity() / 1000));
            jwtCookie.setAttribute("SameSite", "Strict");
            response.addCookie(jwtCookie);

            String redirectUrl;
            switch (role) {
                case "ADMINISTRATEUR":
                    redirectUrl = "/api/dashboard/admin";
                    break;
                case "RECRUTEUR":
                    redirectUrl = "/api/dashboard/recruteur";
                    break;
                case "CANDIDAT":
                    redirectUrl = "/api/dashboard/candidat";
                    break;
                default:
                    logger.error("Rôle inconnu : {}", role);
                    throw new IllegalStateException("Rôle inconnu : " + role);
            }

            logger.info("Connexion réussie pour l'email : {}, rôle : {}, redirection vers : {}", loginRequest.getEmail(), role, redirectUrl);
            return ResponseEntity.ok()
                    .body("{\"message\": \"Connexion réussie\", \"redirectUrl\": \"" + redirectUrl + "\"}");
        } catch (AuthenticationException e) {
            logger.error("Échec de la connexion pour l'email : {}, raison : {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"Identifiants invalides : " + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.error("Erreur interne lors de la connexion pour l'email : {}, erreur : {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erreur interne : " + e.getMessage() + "\"}");
        }
    }
}