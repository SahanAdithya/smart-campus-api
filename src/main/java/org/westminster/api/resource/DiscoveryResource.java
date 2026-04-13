package org.westminster.api.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * Root discovery endpoint for the Smart Campus API.
 * Provides essential API metadata and links to other resources.
 */
@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON) // Using Jackson for JSON
    public Map<String, Object> getDiscovery() {
        Map<String, Object> discoveryMap = new HashMap<>();
        
        // API Versioning
        discoveryMap.put("version", "1.0.0");
        discoveryMap.put("status", "Running");
        
        // Administrative Contact Details
        Map<String, String> contact = new HashMap<>();
        contact.put("name", "Lead Backend Architect");
        contact.put("email", "architect@westminster.ac.uk");
        discoveryMap.put("contact", contact);
        
        // Map of available resources (Hypermedia)
        Map<String, String> resources = new HashMap<>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");
        discoveryMap.put("resources", resources);
        
        discoveryMap.put("message", "Welcome to the Smart Campus API. This is the root discovery endpoint.");
        
        return discoveryMap;
    }
}
