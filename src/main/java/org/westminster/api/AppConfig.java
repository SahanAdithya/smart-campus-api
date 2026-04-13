package org.westminster.api;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * Main application configuration for the Smart Campus API.
 */
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        // Register all API components (resources, mappers, filters)
        packages("org.westminster.api");
        
        // Register Jackson for JSON support
        register(org.glassfish.jersey.jackson.JacksonFeature.class);
    }
}
