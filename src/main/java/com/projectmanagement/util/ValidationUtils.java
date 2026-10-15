package com.projectmanagement.util;

import com.projectmanagement.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Enterprise Validation Utilities for client and server-side verification.
 */
public final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private ValidationUtils() {
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static void validateUserRegistration(String name, String email, String password, boolean isNewUser) {
        List<String> errors = new ArrayList<>();

        if (name == null || name.trim().length() < 2 || name.trim().length() > 100) {
            errors.add("Name must be between 2 and 100 characters.");
        }

        if (!isValidEmail(email)) {
            errors.add("A valid email address is required.");
        }

        if (isNewUser) {
            if (password == null || password.trim().length() < 6) {
                errors.add("Password must be at least 6 characters long.");
            }
        } else if (password != null && !password.trim().isEmpty() && password.trim().length() < 6) {
            errors.add("Password must be at least 6 characters long.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public static void validateProject(String title, java.sql.Date startDate, java.sql.Date dueDate) {
        List<String> errors = new ArrayList<>();

        if (title == null || title.trim().isEmpty()) {
            errors.add("Project title is required.");
        } else if (title.trim().length() > 200) {
            errors.add("Project title cannot exceed 200 characters.");
        }

        if (startDate == null) {
            errors.add("Start date is required.");
        }

        if (dueDate == null) {
            errors.add("Due date is required.");
        }

        if (startDate != null && dueDate != null && dueDate.before(startDate)) {
            errors.add("Due date cannot be earlier than the start date.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public static void validateTask(String title, Long projectId, java.sql.Date startDate, java.sql.Date dueDate) {
        List<String> errors = new ArrayList<>();

        if (title == null || title.trim().isEmpty()) {
            errors.add("Task title is required.");
        } else if (title.trim().length() > 255) {
            errors.add("Task title cannot exceed 255 characters.");
        }

        if (projectId == null || projectId <= 0) {
            errors.add("Associated project is required.");
        }

        if (startDate == null) {
            errors.add("Start date is required.");
        }

        if (dueDate == null) {
            errors.add("Due date is required.");
        }

        if (startDate != null && dueDate != null && dueDate.before(startDate)) {
            errors.add("Task due date cannot be earlier than start date.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
