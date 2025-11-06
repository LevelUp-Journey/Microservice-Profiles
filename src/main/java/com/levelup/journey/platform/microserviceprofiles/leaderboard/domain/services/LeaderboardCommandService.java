package com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.services;

import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.aggregates.LeaderboardEntry;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.commands.RecalculateLeaderboardPositionsCommand;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.commands.UpdateLeaderboardEntryCommand;

import java.util.Optional;

/**
 * Leaderboard Command Service
 * Handles commands related to leaderboard operations
 */
public interface LeaderboardCommandService {

    /**
     * Handle Update Leaderboard Entry Command
     * Updates or creates a leaderboard entry for a user
     *
     * @param command The {@link UpdateLeaderboardEntryCommand}
     * @return Optional of updated/created {@link LeaderboardEntry}
     */
    Optional<LeaderboardEntry> handle(UpdateLeaderboardEntryCommand command);

    /**
     * Handle Recalculate Leaderboard Positions Command
     * Recalculates all leaderboard positions based on current points
     *
     * @param command The {@link RecalculateLeaderboardPositionsCommand}
     * @return Number of entries updated
     */
    Integer handle(RecalculateLeaderboardPositionsCommand command);
}
