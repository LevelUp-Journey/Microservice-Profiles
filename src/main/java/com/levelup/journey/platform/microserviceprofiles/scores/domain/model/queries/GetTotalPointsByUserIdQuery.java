package com.levelup.journey.platform.microserviceprofiles.scores.domain.model.queries;

import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.valueobjects.ScoreUserId;

/**
 * Query to get total points accumulated by a user
 */
public record GetTotalPointsByUserIdQuery(ScoreUserId userId) {
    public GetTotalPointsByUserIdQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
}
