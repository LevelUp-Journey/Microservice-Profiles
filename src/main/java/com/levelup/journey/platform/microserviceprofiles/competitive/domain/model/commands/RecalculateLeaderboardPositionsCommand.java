package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands;

/**
 * Recalculate Leaderboard Positions Command
 * Command to recalculate all leaderboard positions and TOP500 ranks
 */
public record RecalculateLeaderboardPositionsCommand() {
    // Empty record - triggers global leaderboard recalculation
}
