package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record RankName(String name) {
    public RankName {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Rank name cannot be null or blank");
        }
        if (name.length() > 20) {
            throw new IllegalArgumentException("Rank name cannot exceed 20 characters");
        }
    }
}