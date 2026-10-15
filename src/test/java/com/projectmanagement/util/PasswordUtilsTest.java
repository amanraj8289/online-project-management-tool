package com.projectmanagement.util;

import com.projectmanagement.config.PasswordUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilsTest {

    @Test
    @DisplayName("Test BCrypt password hashing and verification")
    void testHashAndVerifyPassword() {
        String plain = "Admin@123";
        String hash = PasswordUtils.hashPassword(plain);

        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"));
        assertTrue(PasswordUtils.verifyPassword(plain, hash));
        assertFalse(PasswordUtils.verifyPassword("WrongPassword", hash));

        System.out.println("BCRYPT HASH FOR Admin@123: " + hash);
        System.out.println("BCRYPT HASH FOR Manager@123: " + PasswordUtils.hashPassword("Manager@123"));
        System.out.println("BCRYPT HASH FOR Member@123: " + PasswordUtils.hashPassword("Member@123"));
    }

    @Test
    @DisplayName("Test null/empty password rejection")
    void testInvalidPasswordHandling() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtils.hashPassword(null));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtils.hashPassword(""));
        assertFalse(PasswordUtils.verifyPassword(null, "somehash"));
        assertFalse(PasswordUtils.verifyPassword("plain", null));
    }
}
