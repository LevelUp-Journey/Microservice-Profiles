package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.CompetitiveRank;

/**
 * Get Users By Rank Query
 * Query to retrieve all users with a specific competitive rank
 */
public record GetUsersByRankQuery(CompetitiveRank rank) {

    public GetUsersByRankQuery {
        if (rank == null) {
            throw new IllegalArgumentException("Rank cannot be null");
        }
    }
}
