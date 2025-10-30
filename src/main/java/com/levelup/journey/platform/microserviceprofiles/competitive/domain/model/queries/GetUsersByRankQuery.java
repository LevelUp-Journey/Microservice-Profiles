package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.CompetitiveRank;

/**
 * Get Users By Rank Query
 * Query to retrieve paginated users with a specific competitive rank
 */
public record GetUsersByRankQuery(CompetitiveRank rank, Integer limit, Integer offset) {

    public GetUsersByRankQuery {
        if (rank == null) {
            throw new IllegalArgumentException("Rank cannot be null");
        }
        if (limit == null || limit < 20 || limit > 20) {
            throw new IllegalArgumentException("Limit must be exactly 20");
        }
        if (offset == null || offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative");
        }
    }
}
