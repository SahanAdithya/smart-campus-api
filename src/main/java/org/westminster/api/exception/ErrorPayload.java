package org.westminster.api.exception;

/**
 * Standard JSON payload for all API error responses.
 */
public class ErrorPayload {
    private String code;
    private String message;

    public ErrorPayload() {}

    public ErrorPayload(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
