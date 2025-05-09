package com.example.demo.Util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class EmailValidator {

    private static final List<String> DOMAINE_JETABLE = Arrays.asList(
            "tempmail.com", "mailinator.com", "guerrillamail.com", "10minutemail.com"
    );

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    public static void validerEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Format d'email invalide");
        }
        String domaine = email.split("@")[1].toLowerCase();
        if (DOMAINE_JETABLE.contains(domaine)) {
            throw new IllegalArgumentException("Les emails jetables ne sont pas autorisés");
        }
    }
}