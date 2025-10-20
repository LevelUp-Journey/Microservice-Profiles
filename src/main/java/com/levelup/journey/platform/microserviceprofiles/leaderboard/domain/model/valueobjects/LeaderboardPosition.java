package com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Leaderboard Position Value Object
 * Represents a user's position in the global leaderboard
 */
@Embeddable
public record LeaderboardPosition(Integer position) {
    public LeaderboardPosition {
        if (position == null || position < 1) {
            throw new IllegalArgumentException("Leaderboard position must be a positive number");
        }
    }

    /**
     * Check if position is in TOP 500
     */
    public boolean isTop500() {
        return position <= 500;
    }
}
