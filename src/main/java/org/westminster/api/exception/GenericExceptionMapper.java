package org.westminster.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Global catch-all ExceptionMapper to prevent stack trace leakage.
 * Maps any unhandled Throwable to 500 Internal Server Error.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        // Log the exception locally (simulated with System.err for this exercise)
        System.err.println("UNEXPECTED ERROR: " + exception.getMessage());
        exception.printStackTrace();

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorPayload("INTERNAL_SERVER_ERROR", 
                        "An unexpected error occurred. Please contact the administrator."))
                .build();
    }
}
