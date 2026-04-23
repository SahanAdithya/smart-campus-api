/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.resource;

/**
 *
 * @author sahanadithya
 */

import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.model.SensorReading;
import com.smartcampus.api.store.InMemoryStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/sensors/{sensorId}/readings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    @GET
    public Response getSensorReadings(@PathParam("sensorId") String sensorId) {
        Sensor sensor = InMemoryStore.sensors.get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sensor not found.")
                    .build();
        }

        List<SensorReading> readings = InMemoryStore.sensorReadings.get(sensorId);
        if (readings == null) {
            readings = new ArrayList<>();
            InMemoryStore.sensorReadings.put(sensorId, readings);
        }

        return Response.ok(readings).build();
    }

    @POST
    public Response addSensorReading(@PathParam("sensorId") String sensorId,
                                     SensorReading reading,
                                     @Context UriInfo uriInfo) {
        Sensor sensor = InMemoryStore.sensors.get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sensor not found.")
                    .build();
        }

        if (sensor.getStatus() != null && "MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Cannot add reading while sensor is in MAINTENANCE.")
                    .build();
        }

        if (reading == null || reading.getValue() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Reading value is required.")
                    .build();
        }

        List<SensorReading> readings = InMemoryStore.sensorReadings.get(sensorId);
        if (readings == null) {
            readings = new ArrayList<>();
            InMemoryStore.sensorReadings.put(sensorId, readings);
        }

        readings.add(reading);

        sensor.setCurrentValue(reading.getValue());
        InMemoryStore.sensors.put(sensorId, sensor);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(readings.size() - 1))
                .build();

        return Response.created(location).entity(reading).build();
    }
}