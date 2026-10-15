package com.projectmanagement.exception;

/**
 * Thrown when an authenticated user attempts to access resources or perform actions
 * outside of their assigned role permissions.
 */
public class AuthorizationException extends AppException {

    private static final long serialVersionUID = 1L;

    public AuthorizationException(String message) {
        super(message);
    }
}
