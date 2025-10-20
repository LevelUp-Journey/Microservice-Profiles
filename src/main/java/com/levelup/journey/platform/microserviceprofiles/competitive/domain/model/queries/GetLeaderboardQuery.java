package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries;

/**
 * Get Leaderboard Query
 * Query to retrieve paginated leaderboard rankings
 */
public record GetLeaderboardQuery(Integer limit, Integer offset) {

    public GetLeaderboardQuery {
        if (limit == null || limit < 1) {
            throw new IllegalArgumentException("Limit must be at least 1");
        }
        if (offset == null || offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative");
        }
    }

    /**
     * Default leaderboard query with standard pagination
     */
    public static GetLeaderboardQuery defaultQuery() {
        return new GetLeaderboardQuery(50, 0);
    }

    /**
     * Query for top N users
     */
    public static GetLeaderboardQuery topN(int n) {
        return new GetLeaderboardQuery(n, 0);
    }
}
