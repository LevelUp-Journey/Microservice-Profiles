package com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.services;

import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.aggregates.LeaderboardEntry;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.queries.GetLeaderboardQuery;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.queries.GetTop500Query;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.queries.GetUserPositionQuery;

import java.util.List;
import java.util.Optional;

/**
 * Leaderboard Query Service
 * Handles queries related to leaderboard retrieval
 */
public interface LeaderboardQueryService {

    /**
     * Handle Get Leaderboard Query
     * Retrieves paginated leaderboard entries
     *
     * @param query The {@link GetLeaderboardQuery}
     * @return List of {@link LeaderboardEntry}
     */
    List<LeaderboardEntry> handle(GetLeaderboardQuery query);

    /**
     * Handle Get User Position Query
     * Retrieves a user's leaderboard entry including position
     *
     * @param query The {@link GetUserPositionQuery}
     * @return Optional of {@link LeaderboardEntry}
     */
    Optional<LeaderboardEntry> handle(GetUserPositionQuery query);

    /**
     * Handle Get Top 500 Query
     * Retrieves the top 500 leaderboard entries
     *
     * @param query The {@link GetTop500Query}
     * @return List of top 500 {@link LeaderboardEntry}
     */
    List<LeaderboardEntry> handle(GetTop500Query query);
}
