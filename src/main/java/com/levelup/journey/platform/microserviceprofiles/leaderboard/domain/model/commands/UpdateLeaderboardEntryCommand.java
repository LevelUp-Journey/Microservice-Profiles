package com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.commands;

/**
 * Update Leaderboard Entry Command
 * Updates or creates a leaderboard entry for a user with new points
 */
public record UpdateLeaderboardEntryCommand(String userId, Integer totalPoints) {
    public UpdateLeaderboardEntryCommand {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (totalPoints == null || totalPoints < 0) {
            throw new IllegalArgumentException("Total points cannot be negative");
        }
    }
}
