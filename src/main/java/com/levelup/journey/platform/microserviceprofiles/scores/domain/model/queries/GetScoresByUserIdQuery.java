package com.levelup.journey.platform.microserviceprofiles.scores.domain.model.queries;

import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.valueobjects.ScoreUserId;

/**
 * Query to get all score records for a specific user
 */
public record GetScoresByUserIdQuery(ScoreUserId userId) {
    public GetScoresByUserIdQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
}
