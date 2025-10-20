package com.levelup.journey.platform.microserviceprofiles.competitive.domain.services;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.aggregates.CompetitiveProfile;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries.GetAllCompetitiveProfilesQuery;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries.GetCompetitiveProfileByUserIdQuery;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries.GetLeaderboardQuery;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries.GetUserRankingPositionQuery;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries.GetUsersByRankQuery;

import java.util.List;
import java.util.Optional;

/**
 * Competitive Profile Query Service
 * Handles queries related to competitive profile retrieval
 */
public interface CompetitiveProfileQueryService {

    /**
     * Handle Get Competitive Profile By User ID Query
     * Retrieves a user's competitive profile
     *
     * @param query The {@link GetCompetitiveProfileByUserIdQuery}
     * @return Optional of {@link CompetitiveProfile}
     */
    Optional<CompetitiveProfile> handle(GetCompetitiveProfileByUserIdQuery query);

    /**
     * Handle Get Leaderboard Query
     * Retrieves paginated leaderboard rankings
     *
     * @param query The {@link GetLeaderboardQuery}
     * @return List of {@link CompetitiveProfile} ordered by rank
     */
    List<CompetitiveProfile> handle(GetLeaderboardQuery query);

    /**
     * Handle Get Users By Rank Query
     * Retrieves all users with a specific competitive rank
     *
     * @param query The {@link GetUsersByRankQuery}
     * @return List of {@link CompetitiveProfile}
     */
    List<CompetitiveProfile> handle(GetUsersByRankQuery query);

    /**
     * Handle Get User Ranking Position Query
     * Retrieves a user's position in the global leaderboard
     *
     * @param query The {@link GetUserRankingPositionQuery}
     * @return Optional of leaderboard position as Integer
     */
    Optional<Integer> handle(GetUserRankingPositionQuery query);

    /**
     * Handle Get All Competitive Profiles Query
     * Retrieves all competitive profiles
     *
     * @param query The {@link GetAllCompetitiveProfilesQuery}
     * @return List of all {@link CompetitiveProfile}
     */
    List<CompetitiveProfile> handle(GetAllCompetitiveProfilesQuery query);
}
