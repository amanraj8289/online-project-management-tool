package com.projectmanagement.config;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Enterprise Password Hashing Utility using BCrypt.
 * Provides salt generation and constant-time verification.
 */
public final class PasswordUtils {

    private static final int BCRYPT_COST = 12;

    private PasswordUtils() {
        // Prevent instantiation
    }

    /**
     * Hash a plaintext password using BCrypt with default cost factor 12.
     *
     * @param plainPassword the plaintext password
     * @return the bcrypt hashed string
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, plainPassword.toCharArray());
    }

    /**
     * Verify a plaintext password against a stored BCrypt hash.
     *
     * @param plainPassword the plaintext candidate password
     * @param hashedPassword the stored bcrypt hash
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword.toCharArray());
        return result.verified;
    }
}
