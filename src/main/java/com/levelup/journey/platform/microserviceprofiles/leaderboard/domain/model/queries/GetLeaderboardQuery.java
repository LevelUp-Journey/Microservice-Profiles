package com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.queries;

/**
 * Get Leaderboard Query
 * Retrieves paginated leaderboard entries ordered by rank
 */
public record GetLeaderboardQuery(Integer limit, Integer offset) {
    public GetLeaderboardQuery {
        if (limit == null || limit < 1 || limit > 500) {
            throw new IllegalArgumentException("Limit must be between 1 and 500");
        }
        if (offset == null || offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative");
        }
    }

    /**
     * Default leaderboard query with limit 50 and offset 0
     */
    public static GetLeaderboardQuery defaultQuery() {
        return new GetLeaderboardQuery(50, 0);
    }
}
