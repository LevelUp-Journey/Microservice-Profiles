package com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.queries;

/**
 * Get Top 500 Query
 * Retrieves paginated top 500 users in the leaderboard (20 per page for API, 500 for internal use)
 */
public record GetTop500Query(Integer limit, Integer offset) {

    public GetTop500Query {
        if (limit == null || (limit != 20 && limit != 500)) {
            throw new IllegalArgumentException("Limit must be 20 (API pagination) or 500 (internal use)");
        }
        if (offset == null || offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative");
        }
        if (offset >= 500) {
            throw new IllegalArgumentException("Offset cannot exceed 500 (TOP 500 limit)");
        }
    }
}
