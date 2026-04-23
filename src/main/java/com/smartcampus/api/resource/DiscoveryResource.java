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
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response discover() {
        String json = "{"
                + "\"service\":\"SmartCampus API\","
                + "\"version\":\"v1\","
                + "\"status\":\"UP\""
                + "}";
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
}