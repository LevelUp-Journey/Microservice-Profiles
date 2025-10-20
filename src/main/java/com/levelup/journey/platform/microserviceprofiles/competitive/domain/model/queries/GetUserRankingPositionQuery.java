package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries;

/**
 * Get User Ranking Position Query
 * Query to retrieve a specific user's position in the global leaderboard
 */
public record GetUserRankingPositionQuery(String userId) {

    public GetUserRankingPositionQuery {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
    }
}
