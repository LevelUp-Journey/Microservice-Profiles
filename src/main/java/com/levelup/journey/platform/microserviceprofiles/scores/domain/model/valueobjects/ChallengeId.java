package com.levelup.journey.platform.microserviceprofiles.scores.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value object representing a challenge ID from external system
 */
@Embeddable
public record ChallengeId(String challengeId) {
    
    public ChallengeId {
        if (challengeId == null || challengeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Challenge ID cannot be null or empty");
        }
        if (challengeId.length() > 100) {
            throw new IllegalArgumentException("Challenge ID cannot exceed 100 characters");
        }
    }

    public ChallengeId() {
        this(null);
    }
}
