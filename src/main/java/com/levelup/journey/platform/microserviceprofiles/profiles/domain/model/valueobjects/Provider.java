package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value object representing the authentication provider
 */
@Embeddable
public record Provider(String value) {

    public Provider {
        if (value == null || value.trim().isEmpty()) {
            value = "local"; // Default to local for backward compatibility
        }
        validateProvider(value.trim());
    }

    private static void validateProvider(String provider) {
        if (provider.length() > 50) {
            throw new IllegalArgumentException("Provider name cannot exceed 50 characters");
        }
        // Allow common providers: local, google, facebook, github, etc.
        // For now, allow any string, but could add specific validation later
    }

    public String value() {
        return value;
    }
}