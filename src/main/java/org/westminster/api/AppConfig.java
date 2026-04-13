package org.westminster.api;

import org.glassfish.jersey.server.ResourceConfig;
import javax.ws.rs.ApplicationPath;

/**
 * Main application configuration for the Smart Campus API.
 * The @ApplicationPath sets the base URI for all resources to /api/v1.
 */
@ApplicationPath("/api/v1")
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        // Register all API components (resources, mappers, filters)
        packages("org.westminster.api");
        
        // Register Jackson for JSON support
        register(org.glassfish.jersey.jackson.JacksonFeature.class);
    }
}
