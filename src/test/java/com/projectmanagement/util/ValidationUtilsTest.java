package com.projectmanagement.util;

import com.projectmanagement.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilsTest {

    @Test
    @DisplayName("Test email pattern validation")
    void testEmailValidation() {
        assertTrue(ValidationUtils.isValidEmail("admin@promanage.com"));
        assertTrue(ValidationUtils.isValidEmail("user.name+tag@example.co.uk"));
        assertFalse(ValidationUtils.isValidEmail("invalid-email"));
        assertFalse(ValidationUtils.isValidEmail("missing@domain"));
        assertFalse(ValidationUtils.isValidEmail(null));
        assertFalse(ValidationUtils.isValidEmail(""));
    }

    @Test
    @DisplayName("Test user registration validation")
    void testUserRegistrationValidation() {
        assertDoesNotThrow(() ->
                ValidationUtils.validateUserRegistration("Jane Doe", "jane@promanage.com", "Password@123", true));

        assertThrows(ValidationException.class, () ->
                ValidationUtils.validateUserRegistration("A", "jane@promanage.com", "Password@123", true));

        assertThrows(ValidationException.class, () ->
                ValidationUtils.validateUserRegistration("Jane Doe", "invalid-email", "Password@123", true));

        assertThrows(ValidationException.class, () ->
                ValidationUtils.validateUserRegistration("Jane Doe", "jane@promanage.com", "123", true));
    }

    @Test
    @DisplayName("Test project date validation (due date cannot precede start date)")
    void testProjectValidation() {
        Date start = Date.valueOf(LocalDate.now());
        Date due = Date.valueOf(LocalDate.now().plusDays(10));
        Date invalidDue = Date.valueOf(LocalDate.now().minusDays(5));

        assertDoesNotThrow(() ->
                ValidationUtils.validateProject("Cloud Migration", start, due));

        assertThrows(ValidationException.class, () ->
                ValidationUtils.validateProject("Cloud Migration", start, invalidDue));

        assertThrows(ValidationException.class, () ->
                ValidationUtils.validateProject("", start, due));
    }

    @Test
    @DisplayName("Test task validation")
    void testTaskValidation() {
        Date start = Date.valueOf(LocalDate.now());
        Date due = Date.valueOf(LocalDate.now().plusDays(5));

        assertDoesNotThrow(() ->
                ValidationUtils.validateTask("Build Auth Module", 1L, start, due));

        assertThrows(ValidationException.class, () ->
                ValidationUtils.validateTask("", 1L, start, due));

        assertThrows(ValidationException.class, () ->
                ValidationUtils.validateTask("Task 1", null, start, due));
    }
}
