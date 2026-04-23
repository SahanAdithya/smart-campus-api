/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.resource;

/**
 *
 * @author sahanadithya
 */

import com.smartcampus.api.model.Room;
import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.store.InMemoryStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> result = new ArrayList<>();

        for (Sensor sensor : InMemoryStore.sensors.values()) {
            if (type == null || type.trim().isEmpty()) {
                result.add(sensor);
            } else if (sensor.getType() != null && sensor.getType().equalsIgnoreCase(type.trim())) {
                result.add(sensor);
            }
        }

        return Response.ok(result).build();
    }

    @POST
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        if (sensor == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sensor body is required.")
                    .build();
        }

        String id = sensor.getId();

        if (id == null || id.trim().isEmpty()) {
            id = UUID.randomUUID().toString();
        } else {
            id = id.trim();
        }

        if (InMemoryStore.sensors.containsKey(id)) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Sensor with id '" + id + "' already exists.")
                    .build();
        }

        if (sensor.getRoomId() == null || !InMemoryStore.rooms.containsKey(sensor.getRoomId())) {
            return Response.status(422)
                    .entity("Room with id '" + sensor.getRoomId() + "' does not exist.")
                    .build();
        }

        sensor.setId(id);
        InMemoryStore.sensors.put(id, sensor);
        InMemoryStore.sensorReadings.put(id, new ArrayList<>());

        Room room = InMemoryStore.rooms.get(sensor.getRoomId());
        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<String>());
        }
        if (!room.getSensorIds().contains(id)) {
            room.getSensorIds().add(id);
        }

        URI location = uriInfo.getAbsolutePathBuilder().path(id).build();
        return Response.created(location).entity(sensor).build();
    }

    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = InMemoryStore.sensors.get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sensor not found.")
                    .build();
        }

        return Response.ok(sensor).build();
    }

    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = InMemoryStore.sensors.get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sensor not found.")
                    .build();
        }

        Room room = InMemoryStore.rooms.get(sensor.getRoomId());
        if (room != null && room.getSensorIds() != null) {
            room.getSensorIds().remove(sensorId);
        }

        InMemoryStore.sensors.remove(sensorId);
        InMemoryStore.sensorReadings.remove(sensorId);

        return Response.ok("Sensor deleted successfully.").build();
    }
}