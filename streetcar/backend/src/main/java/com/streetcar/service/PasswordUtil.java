package com.streetcar.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Utilitário simples — NÃO é um @Component para não conflitar com o
// PasswordEncoder bean definido em SecurityConfig.
public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
