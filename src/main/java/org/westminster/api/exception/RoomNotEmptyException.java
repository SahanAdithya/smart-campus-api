package org.westminster.api.exception;

/**
 * Exception thrown when a room cannot be deleted because it still contains hardware.
 * Should map to HTTP 409 Conflict.
 */
public class RoomNotEmptyException extends RuntimeException {
    public RoomNotEmptyException(String message) {
        super(message);
    }
}
