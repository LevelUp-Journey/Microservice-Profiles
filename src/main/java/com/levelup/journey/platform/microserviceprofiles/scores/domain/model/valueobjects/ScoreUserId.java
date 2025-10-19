package com.levelup.journey.platform.microserviceprofiles.scores.domain.model.valueobjects;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.UserId;
import jakarta.persistence.Embeddable;

/**
 * Value object representing the external UserId reference for scores
 * This prevents circular dependencies with the profiles bounded context
 */
@Embeddable
public record ScoreUserId(String userId) {
    
    public ScoreUserId {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
    }

    public ScoreUserId() {
        this(null);
    }

    /**
     * Convert from profiles UserId to ScoreUserId
     */
    public static ScoreUserId from(UserId userId) {
        return new ScoreUserId(userId.userId());
    }
}
