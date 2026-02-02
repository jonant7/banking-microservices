package com.banking.customer.application.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PasswordHashingService {

    private final PasswordEncoder passwordEncoder;

    public PasswordHashingService() {
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    public String hashPassword(String plainPassword) {
        if (Objects.isNull(plainPassword) || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return passwordEncoder.encode(plainPassword);
    }

    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (Objects.isNull(plainPassword) || Objects.isNull(hashedPassword)) {
            return false;
        }
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }

}