package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands;

import java.util.UUID;

public record CreateProfileRankCommand(UUID profileId) {
    public CreateProfileRankCommand {
        if (profileId == null) {
            throw new IllegalArgumentException("Profile ID cannot be null");
        }
    }
}