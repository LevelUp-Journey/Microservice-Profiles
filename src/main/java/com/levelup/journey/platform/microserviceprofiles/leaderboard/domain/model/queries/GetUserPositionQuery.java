package com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.queries;

/**
 * Get User Position Query
 * Retrieves a specific user's position in the leaderboard
 */
public record GetUserPositionQuery(String userId) {
    public GetUserPositionQuery {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
    }
}
