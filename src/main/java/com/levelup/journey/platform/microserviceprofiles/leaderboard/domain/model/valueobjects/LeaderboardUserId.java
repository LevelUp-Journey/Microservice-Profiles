package com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Leaderboard User ID Value Object
 * Represents a user identifier in the leaderboard context
 */
@Embeddable
public record LeaderboardUserId(String userId) {
    public LeaderboardUserId {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
    }
}
