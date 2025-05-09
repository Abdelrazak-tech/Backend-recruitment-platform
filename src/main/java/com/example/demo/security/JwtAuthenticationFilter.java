package com.example.demo.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.Util.JwtUtil;

import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    // Liste des endpoints publics à ignorer
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/api/auth/",
        "/api/inscription/",
        "/api/inscription/verifier-email",
        "/api/verifier-email", // Ajouté
        "/error" // Autoriser /error
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        logger.info("Vérification si le filtre doit être appliqué pour l'URI : {}", requestURI);

        boolean shouldNotFilter = PUBLIC_ENDPOINTS.stream().anyMatch(endpoint -> {
            boolean match = requestURI.startsWith(endpoint);
            logger.info("Comparaison URI {} avec endpoint {} : {}", requestURI, endpoint, match);
            return match;
        });
        logger.info("shouldNotFilter pour {} : {}", requestURI, shouldNotFilter);
        return shouldNotFilter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        logger.info("Application du filtre JWT pour l'URI : {}", request.getRequestURI());

        String jwt = null;
        String email = null;

        // Extraire le token du cookie jwt_token
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt_token".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    logger.info("Token JWT trouvé dans le cookie : {}", jwt);
                    break;
                }
            }
        } else {
            logger.info("Aucun cookie trouvé dans la requête pour l'URI : {}", request.getRequestURI());
        }

        if (jwt != null) {
            try {
                email = jwtUtil.extractEmail(jwt);
                logger.info("Email extrait du token : {}", email);
            } catch (SignatureException e) {
                logger.error("Signature JWT invalide pour l'URI : {}", request.getRequestURI(), e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature");
                return;
            } catch (Exception e) {
                logger.error("Token JWT invalide pour l'URI : {}", request.getRequestURI(), e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
            if (jwtUtil.validateToken(jwt, email)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("Utilisateur authentifié avec succès : {}", email);
            } else {
                logger.warn("Token JWT non valide pour l'email : {}", email);
            }
        }

        chain.doFilter(request, response);
    }
}