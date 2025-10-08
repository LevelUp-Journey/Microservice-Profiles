package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

import java.util.UUID;

/**
 * Value object representing User ID from IAM context
 */
@Embeddable
public record UserId(String userId) {
    public UserId {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        try {
            UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("User ID must be a valid UUID format");
        }
    }
}
