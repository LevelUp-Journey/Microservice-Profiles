package com.levelup.journey.platform.microserviceprofiles.leaderboard.application.internal.eventhandlers;

import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.commands.UpdateLeaderboardEntryCommand;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.services.LeaderboardCommandService;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.events.ScoreUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Score Updated Event Handler
 * Handles score update events from Scores BC to update leaderboard
 */
@Service("leaderboardScoreUpdatedEventHandler")
public class ScoreUpdatedEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(ScoreUpdatedEventHandler.class);
    private final LeaderboardCommandService leaderboardCommandService;

    public ScoreUpdatedEventHandler(LeaderboardCommandService leaderboardCommandService) {
        this.leaderboardCommandService = leaderboardCommandService;
    }

    /**
     * Handle ScoreUpdatedEvent
     * Updates leaderboard entry when user's score changes
     * Also accumulates execution time from challenge completions
     *
     * @param event The ScoreUpdatedEvent
     */
    @EventListener
    @Async
    public void on(ScoreUpdatedEvent event) {
        logger.info("Received ScoreUpdatedEvent for user {} with {} total points and {} ms execution time",
                event.getUserId(), event.getNewTotalPoints(), event.getExecutionTimeMs());

        try {
            // Update leaderboard entry with new total points
            var command = new UpdateLeaderboardEntryCommand(
                    event.getUserId(),
                    event.getNewTotalPoints()
            );

            var updatedEntry = leaderboardCommandService.handle(command);

            if (updatedEntry.isPresent()) {
                // Accumulate execution time from this challenge (converts from ms to seconds)
                var entry = updatedEntry.get();
                entry.accumulateExecutionTime(event.getExecutionTimeMs());

                logger.info("Updated leaderboard entry for user {} - Position: {}, Points: {}, Total Time: {} seconds",
                        event.getUserId(),
                        entry.getPosition(),
                        entry.getTotalPoints(),
                        entry.getTotalTimeToAchievePointsMs());
            } else {
                logger.warn("Failed to update leaderboard entry for user {}", event.getUserId());
            }

        } catch (Exception e) {
            logger.error("Error handling ScoreUpdatedEvent for user {}: {}",
                    event.getUserId(), e.getMessage(), e);
        }
    }
}
