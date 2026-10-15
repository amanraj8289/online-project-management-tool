package com.projectmanagement.exception;

/**
 * Wraps low-level SQLExceptions to prevent leaking database internals
 * to the presentation layer while logging full diagnostic context.
 */
public class DatabaseException extends AppException {

    private static final long serialVersionUID = 1L;

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
