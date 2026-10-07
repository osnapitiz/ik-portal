package com.pmt.ikportal.service;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * PBKDF2 tabanli sifre hashleme. Sifreler veritabaninda asla duz metin tutulmaz.
 */
@Component
public class PasswordHasher {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;

    private final SecureRandom random = new SecureRandom();

    public String newSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public String hash(String rawPassword, String salt) {
        try {
            KeySpec spec = new PBEKeySpec(
                    rawPassword.toCharArray(),
                    Base64.getDecoder().decode(salt),
                    ITERATIONS,
                    KEY_LENGTH);
            byte[] key = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(key);
        } catch (Exception e) {
            throw new IllegalStateException("Sifre hashlenemedi", e);
        }
    }

    public boolean matches(String rawPassword, String salt, String expectedHash) {
        String actual = hash(rawPassword, salt);
        return constantTimeEquals(actual, expectedHash);
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            diff |= a.charAt(i) ^ b.charAt(i);
        }
        return diff == 0;
    }
}
