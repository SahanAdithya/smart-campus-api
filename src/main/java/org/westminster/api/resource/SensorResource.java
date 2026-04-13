package org.westminster.api.resource;

import org.westminster.api.exception.LinkedResourceNotFoundException;
import org.westminster.api.model.Sensor;
import org.westminster.api.repository.DataStore;
import org.westminster.api.resource.SensorReadingResource;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

/**
 * Resource class for managing Sensors.
 * Base path is /sensors (relative to /api/v1).
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore dataStore = DataStore.getInstance();

    /**
     * GET /api/v1/sensors
     * Supports an optional query parameter 'type' to filter sensors.
     * Example: GET /api/v1/sensors?type=CO2
     */
    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        if (type != null && !type.isEmpty()) {
            return dataStore.getSensorsByType(type);
        }
        return dataStore.getAllSensors();
    }

    /**
     * POST /api/v1/sensors
     * Registers a new sensor.
     * Validates that the associated roomId exists.
     */
    @POST
    public Response registerSensor(Sensor sensor, @Context UriInfo uriInfo) {
        // Validate roomId exists
        if (sensor.getRoomId() == null || dataStore.getRoom(sensor.getRoomId()) == null) {
            throw new LinkedResourceNotFoundException("Invalid roomId: Reference to room '" + sensor.getRoomId() + "' does not exist.");
        }

        // Basic validation for sensor ID
        if (sensor.getId() == null || sensor.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage("Sensor ID is required.")).build();
        }

        // Check for duplicate sensor ID
        if (dataStore.getSensor(sensor.getId()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorMessage("Sensor with ID " + sensor.getId() + " already exists.")).build();
        }

        dataStore.addSensor(sensor);

        // Build location URI
        URI uri = uriInfo.getAbsolutePathBuilder().path(sensor.getId()).build();
        return Response.created(uri).entity(sensor).build();
    }

    /**
     * Sub-resource locator for readings.
     * Routes requests from {sensorId}/readings to SensorReadingResource.
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }

    /**
     * Helper class for JSON error responses.
     */
    public static class ErrorMessage {
        private String error;
        public ErrorMessage(String error) { this.error = error; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}
