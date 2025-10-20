package com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.commands;

/**
 * Recalculate Leaderboard Positions Command
 * Triggers a full recalculation of all leaderboard positions based on current points
 */
public record RecalculateLeaderboardPositionsCommand() {
    // No parameters needed - operates on all entries
}
