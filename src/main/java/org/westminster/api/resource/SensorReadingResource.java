package org.westminster.api.resource;

import org.westminster.api.model.SensorReading;
import org.westminster.api.repository.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

/**
 * Sub-resource for managing readings of a specific sensor.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final DataStore dataStore = DataStore.getInstance();
    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * GET /api/v1/sensors/{sensorId}/readings
     * Returns history of readings for this sensor.
     */
    @GET
    public Response getReadings() {
        if (dataStore.getSensor(sensorId) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sensor not found: " + sensorId).build();
        }
        List<SensorReading> history = dataStore.getReadings(sensorId);
        return Response.ok(history).build();
    }

    /**
     * POST /api/v1/sensors/{sensorId}/readings
     * Appends a new reading and triggers a state update on the parent sensor.
     */
    @POST
    public Response addReading(SensorReading reading, @Context UriInfo uriInfo) {
        if (dataStore.getSensor(sensorId) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sensor not found: " + sensorId).build();
        }

        if (reading.getId() == null || reading.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Reading ID is required").build();
        }

        // Add reading via DataStore (which handles the side-effect update)
        dataStore.addReading(sensorId, reading);

        URI uri = uriInfo.getAbsolutePathBuilder().path(reading.getId()).build();
        return Response.created(uri).entity(reading).build();
    }
}
