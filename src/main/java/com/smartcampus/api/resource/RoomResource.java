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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<>(InMemoryStore.rooms.values());
    }

    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        if (room == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Room body is required.")
                    .build();
        }

        String id = room.getId();

        if (id == null || id.trim().isEmpty()) {
            id = UUID.randomUUID().toString();
        } else {
            id = id.trim();
        }

        if (InMemoryStore.rooms.containsKey(id)) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Room with id '" + id + "' already exists.")
                    .build();
        }

        room.setId(id);

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<String>());
        }

        InMemoryStore.rooms.put(id, room);

        URI location = uriInfo.getAbsolutePathBuilder().path(id).build();
        return Response.created(location).entity(room).build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = InMemoryStore.rooms.get(roomId);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Room with id '" + roomId + "' not found.")
                    .build();
        }

        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = InMemoryStore.rooms.get(roomId);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Room with id '" + roomId + "' not found.")
                    .build();
        }

        for (Sensor sensor : InMemoryStore.sensors.values()) {
            if (sensor != null
                    && sensor.getRoomId() != null
                    && roomId.equals(sensor.getRoomId())) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Cannot delete room because sensors are still assigned to it.")
                        .build();
            }
        }

        InMemoryStore.rooms.remove(roomId);
        return Response.ok("Room deleted successfully.").build();
    }
}