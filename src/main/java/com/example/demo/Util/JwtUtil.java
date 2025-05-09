package com.example.demo.Util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final SecretKey SECRET_KEY;
    private long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000; // 5 heures

    private static final Path SECRET_KEY_FILE = Paths.get("jwt-secret.txt");

    public JwtUtil() {
        try {
            // Vérifier si une clé existe déjà dans le fichier
            if (Files.exists(SECRET_KEY_FILE)) {
                String secretKeyBase64 = Files.readString(SECRET_KEY_FILE).trim();
                this.SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKeyBase64));
                System.out.println("Clé secrète chargée depuis le fichier : " + secretKeyBase64);
            } else {
                // Générer une nouvelle clé et la sauvegarder
                this.SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
                String secretKeyBase64 = Base64.getEncoder().encodeToString(SECRET_KEY.getEncoded());
                Files.writeString(SECRET_KEY_FILE, secretKeyBase64);
                System.out.println("Nouvelle clé secrète générée et sauvegardée : " + secretKeyBase64);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors de la gestion de la clé secrète : " + e.getMessage(), e);
        }
    }

    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SECRET_KEY)
                .compact();
    }

    public Boolean validateToken(String token, String email) {
        try {
            String extractedEmail = extractEmail(token);
            return extractedEmail.equals(email) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public long getJwtTokenValidity() {
        return JWT_TOKEN_VALIDITY;
    }
}