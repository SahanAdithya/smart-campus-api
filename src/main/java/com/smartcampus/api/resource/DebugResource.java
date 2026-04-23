/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.resource;

/**
 *
 * @author sahanadithya
 */

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/debug")
public class DebugResource {

    @GET
    @Path("/error")
    public String triggerError() {
        throw new RuntimeException("Simulated internal server error");
    }
}