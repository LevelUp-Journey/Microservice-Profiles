package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record ScoreChangeReason(String reason) {
    public ScoreChangeReason {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Score change reason cannot be null or blank");
        }
        if (reason.length() > 255) {
            throw new IllegalArgumentException("Score change reason cannot exceed 255 characters");
        }
    }
}