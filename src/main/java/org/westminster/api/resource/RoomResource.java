package org.westminster.api.resource;

import org.westminster.api.exception.RoomNotEmptyException;
import org.westminster.api.model.Room;
import org.westminster.api.repository.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

/**
 * Resource class for managing Rooms.
 * Base path is /rooms (relative to /api/v1).
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final DataStore dataStore = DataStore.getInstance();

    /**
     * GET /api/v1/rooms
     * Returns a comprehensive list of all rooms.
     */
    @GET
    public List<Room> getAllRooms() {
        // Implementation Note: Returning full objects for better client utility, 
        // as opposed to ID-only which reduces bandwidth but increases round-trips.
        return dataStore.getAllRooms();
    }

    /**
     * POST /api/v1/rooms
     * Creates a new room. Returns 201 Created with Location header.
     */
    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        if (room.getId() == null || room.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Room ID is required").build();
        }
        
        if (dataStore.getRoom(room.getId()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Room with this ID already exists").build();
        }
        
        dataStore.addRoom(room);
        
        // Build the URI for the new resource
        URI uri = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();
        return Response.created(uri).entity(room).build();
    }

    /**
     * GET /api/v1/rooms/{roomId}
     * Fetches detailed metadata for a specific room.
     */
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = dataStore.getRoom(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Room not found with ID: " + roomId).build();
        }
        return Response.ok(room).build();
    }

    /**
     * DELETE /api/v1/rooms/{roomId}
     * Deletes a room if no active sensors are assigned.
     */
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = dataStore.getRoom(roomId);
        
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Room not found").build();
        }
        
        // Business Rule: Check if room has sensors
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room " + roomId + " cannot be deleted as it has active sensors assigned.");
        }
        
        boolean deleted = dataStore.deleteRoom(roomId);
        if (deleted) {
            return Response.noContent().build(); // 204 No Content
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to delete room").build();
        }
    }
    
    // Simple inner class for JSON error responses
    public static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
