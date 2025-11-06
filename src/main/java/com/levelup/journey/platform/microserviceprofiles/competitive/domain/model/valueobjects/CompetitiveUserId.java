package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Competitive User ID Value Object
 * Represents a reference to a user in the competitive context
 */
@Embeddable
public record CompetitiveUserId(String userId) {

    public CompetitiveUserId {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
    }
}
