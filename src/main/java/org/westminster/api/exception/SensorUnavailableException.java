package org.westminster.api.exception;

/**
 * Exception thrown when a sensor is in a state that prevents it from accepting new data.
 * Should map to HTTP 403 Forbidden.
 */
public class SensorUnavailableException extends RuntimeException {
    public SensorUnavailableException(String message) {
        super(message);
    }
}
