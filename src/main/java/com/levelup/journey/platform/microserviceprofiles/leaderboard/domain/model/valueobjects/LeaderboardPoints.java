package com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Leaderboard Points Value Object
 * Represents total points for leaderboard ranking
 */
@Embeddable
public record LeaderboardPoints(Integer points) {
    public LeaderboardPoints {
        if (points == null || points < 0) {
            throw new IllegalArgumentException("Leaderboard points cannot be negative");
        }
    }

    /**
     * Create LeaderboardPoints with zero value
     */
    public static LeaderboardPoints zero() {
        return new LeaderboardPoints(0);
    }
}
