package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands;

import java.util.UUID;

public record AddScoreCommand(
    UUID profileId,
    Integer points,
    String reason,
    String externalReferenceId
) {
    public AddScoreCommand {
        if (profileId == null) {
            throw new IllegalArgumentException("Profile ID cannot be null");
        }
        if (points == null || points <= 0) {
            throw new IllegalArgumentException("Points must be positive");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason cannot be null or blank");
        }
    }
}