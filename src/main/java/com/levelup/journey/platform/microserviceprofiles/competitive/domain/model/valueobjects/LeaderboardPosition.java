package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Leaderboard Position Value Object
 * Represents a user's position in the global leaderboard
 */
@Embeddable
public record LeaderboardPosition(Integer position) {

    public LeaderboardPosition {
        if (position == null || position < 1) {
            throw new IllegalArgumentException("Leaderboard position must be a positive number starting from 1");
        }
    }

    /**
     * Check if this position qualifies for TOP500 rank
     * @return true if position <= 500
     */
    public boolean isTop500() {
        return position <= 500;
    }

    /**
     * Compare positions
     * @param other Other position
     * @return negative if this position is better (lower number)
     */
    public int compareTo(LeaderboardPosition other) {
        return this.position.compareTo(other.position);
    }

    /**
     * Check if this position is better than another
     * @param other Other position
     * @return true if this position is numerically lower (better)
     */
    public boolean isBetterThan(LeaderboardPosition other) {
        return this.position < other.position;
    }
}
