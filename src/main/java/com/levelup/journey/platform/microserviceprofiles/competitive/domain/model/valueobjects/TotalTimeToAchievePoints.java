package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Total Time To Achieve Points Value Object
 * Represents the cumulative time (in seconds) it took for a user to complete all challenges
 * Synchronized from Leaderboard BC
 * Stored as BIGINT in database (seconds, not milliseconds)
 */
@Embeddable
public record TotalTimeToAchievePoints(Long seconds) {
    public TotalTimeToAchievePoints {
        if (seconds == null || seconds < 0) {
            throw new IllegalArgumentException("Total time to achieve points cannot be negative");
        }
    }

    /**
     * Create TotalTimeToAchievePoints with zero value
     */
    public static TotalTimeToAchievePoints zero() {
        return new TotalTimeToAchievePoints(0L);
    }

    /**
     * Create TotalTimeToAchievePoints from milliseconds
     * @param milliseconds Time in milliseconds
     * @return TotalTimeToAchievePoints with time converted to seconds
     */
    public static TotalTimeToAchievePoints fromMilliseconds(Long milliseconds) {
        if (milliseconds == null || milliseconds < 0) {
            throw new IllegalArgumentException("Milliseconds cannot be negative");
        }
        return new TotalTimeToAchievePoints(milliseconds / 1000);
    }

    /**
     * Get time in milliseconds
     * @return Time converted from seconds to milliseconds
     */
    public Long toMilliseconds() {
        return this.seconds * 1000;
    }

    /**
     * Add time to current time (accumulate)
     * @param additionalSeconds Additional time in seconds
     * @return New TotalTimeToAchievePoints with accumulated time
     */
    public TotalTimeToAchievePoints add(Long additionalSeconds) {
        if (additionalSeconds == null || additionalSeconds < 0) {
            throw new IllegalArgumentException("Additional time cannot be negative");
        }
        return new TotalTimeToAchievePoints(this.seconds + additionalSeconds);
    }
}
