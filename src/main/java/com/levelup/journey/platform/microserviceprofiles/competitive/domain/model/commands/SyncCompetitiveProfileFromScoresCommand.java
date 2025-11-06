package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands;

/**
 * Sync Competitive Profile From Scores Command
 * Command to synchronize a user's competitive profile with their current score totals
 */
public record SyncCompetitiveProfileFromScoresCommand(String userId) {

    public SyncCompetitiveProfileFromScoresCommand {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
    }
}
