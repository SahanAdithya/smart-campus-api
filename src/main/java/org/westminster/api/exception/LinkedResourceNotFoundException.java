package org.westminster.api.exception;

/**
 * Exception thrown when a reference to another resource (e.g., roomId) is invalid.
 * Should map to HTTP 422 Unprocessable Entity.
 */
public class LinkedResourceNotFoundException extends RuntimeException {
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}
