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
        // Register resources by package
        packages("org.westminster.api.resource");
        
        // Register Jackson for JSON support
        register(org.glassfish.jersey.jackson.JacksonFeature.class);
    }
}
