/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.exception;

/**
 *
 * @author sahanadithya
 */

import com.smartcampus.api.model.ApiError;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof WebApplicationException) {
            WebApplicationException webEx = (WebApplicationException) exception;
            int status = webEx.getResponse().getStatus();

            String error;
            if (status == 422) {
                error = "Unprocessable Entity";
            } else if (Response.Status.fromStatusCode(status) != null) {
                error = Response.Status.fromStatusCode(status).getReasonPhrase();
            } else {
                error = "Request Error";
            }

            ApiError body = new ApiError(
                    status,
                    error,
                    exception.getMessage() == null ? "Request failed." : exception.getMessage()
            );

            return Response.status(status)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(body)
                    .build();
        }

        ApiError body = new ApiError(
                500,
                "Internal Server Error",
                "Something went wrong."
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}