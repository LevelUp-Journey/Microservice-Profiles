package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries;

/**
 * Search Users By Username Query
 * Represents a request to search for users by username pattern (case-insensitive)
 */
public record SearchUsersByUsernameQuery(String usernamePattern) {
    public SearchUsersByUsernameQuery {
        if (usernamePattern == null || usernamePattern.isBlank()) {
            throw new IllegalArgumentException("Username pattern cannot be null or blank");
        }
        if (usernamePattern.length() < 2) {
            throw new IllegalArgumentException("Username pattern must be at least 2 characters");
        }
    }
}
