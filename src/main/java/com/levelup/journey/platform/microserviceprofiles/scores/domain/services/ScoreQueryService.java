package com.levelup.journey.platform.microserviceprofiles.scores.domain.services;

import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.aggregates.Score;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.queries.GetAllScoresQuery;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.queries.GetScoresByUserIdQuery;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.queries.GetTotalPointsByUserIdQuery;

import java.util.List;

/**
 * Score Query Service
 * Handles queries related to score retrieval
 */
public interface ScoreQueryService {
    
    /**
     * Handle Get Scores By User ID Query
     * Retrieves all score records for a specific user
     *
     * @param query The {@link GetScoresByUserIdQuery} Query
     * @return List of {@link Score} instances
     */
    List<Score> handle(GetScoresByUserIdQuery query);

    /**
     * Handle Get Total Points By User ID Query
     * Calculates and retrieves the total points for a specific user
     *
     * @param query The {@link GetTotalPointsByUserIdQuery} Query
     * @return Total points as Integer
     */
    Integer handle(GetTotalPointsByUserIdQuery query);

    /**
     * Handle Get All Scores Query
     * Retrieves all score records in the system
     *
     * @param query The {@link GetAllScoresQuery} Query
     * @return List of all {@link Score} instances
     */
    List<Score> handle(GetAllScoresQuery query);
}
