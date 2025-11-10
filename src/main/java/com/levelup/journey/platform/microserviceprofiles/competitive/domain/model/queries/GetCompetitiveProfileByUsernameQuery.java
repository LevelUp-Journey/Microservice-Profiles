package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries;

/**
 * Get Competitive Profile By Username Query
 * Query to retrieve a user's competitive profile by their username
 */
public record GetCompetitiveProfileByUsernameQuery(String username) {

    public GetCompetitiveProfileByUsernameQuery {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
    }
}
