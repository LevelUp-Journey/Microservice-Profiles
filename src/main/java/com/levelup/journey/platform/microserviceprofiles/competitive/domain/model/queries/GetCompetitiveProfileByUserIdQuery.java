package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries;

/**
 * Get Competitive Profile By User ID Query
 * Query to retrieve a user's competitive profile
 */
public record GetCompetitiveProfileByUserIdQuery(String userId) {

    public GetCompetitiveProfileByUserIdQuery {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
    }
}
