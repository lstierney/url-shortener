package com.tpximpact.urlshortener.controller;

public interface HealthController {
    /**
     * Returns the health status of the application.
     *
     * @return a simple status string, e.g., "ok"
     */
    String health();
}
