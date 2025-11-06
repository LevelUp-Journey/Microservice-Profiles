package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Total Points Value Object
 * Represents the total accumulated points for competitive ranking
 */
@Embeddable
public record TotalPoints(Integer value) {

    public TotalPoints {
        if (value == null || value < 0) {
            throw new IllegalArgumentException("Total points cannot be null or negative");
        }
    }

    /**
     * Create TotalPoints with zero value
     */
    public static TotalPoints zero() {
        return new TotalPoints(0);
    }

    /**
     * Add points to current total
     * @param pointsToAdd Points to add
     * @return New TotalPoints instance with updated value
     */
    public TotalPoints add(Integer pointsToAdd) {
        if (pointsToAdd == null || pointsToAdd < 0) {
            throw new IllegalArgumentException("Points to add must be positive");
        }
        return new TotalPoints(this.value + pointsToAdd);
    }

    /**
     * Check if points meet minimum threshold
     * @param minimum Minimum required points
     * @return true if current points >= minimum
     */
    public boolean meetsMinimum(Integer minimum) {
        return this.value >= minimum;
    }
}
