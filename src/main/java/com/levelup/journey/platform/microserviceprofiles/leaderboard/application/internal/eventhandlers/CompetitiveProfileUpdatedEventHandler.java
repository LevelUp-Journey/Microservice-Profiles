package com.levelup.journey.platform.microserviceprofiles.leaderboard.application.internal.eventhandlers;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.events.CompetitiveProfileUpdatedEvent;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.commands.UpdateLeaderboardEntryCommand;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.services.LeaderboardCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Competitive Profile Updated Event Handler
 * Handles competitive profile update events to synchronize leaderboard entries
 */
@Service
public class CompetitiveProfileUpdatedEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(CompetitiveProfileUpdatedEventHandler.class);
    private final LeaderboardCommandService leaderboardCommandService;

    public CompetitiveProfileUpdatedEventHandler(LeaderboardCommandService leaderboardCommandService) {
        this.leaderboardCommandService = leaderboardCommandService;
    }

    /**
     * Handle CompetitiveProfileUpdatedEvent
     * Updates leaderboard entry when competitive profile is synchronized
     *
     * @param event The CompetitiveProfileUpdatedEvent
     */
    @EventListener
    @Async
    public void on(CompetitiveProfileUpdatedEvent event) {
        logger.info("Received CompetitiveProfileUpdatedEvent for user {} with {} total points and rank {}",
                event.getUserId(), event.getTotalPoints(), event.getRankName());

        try {
            // Update leaderboard entry with new total points
            var command = new UpdateLeaderboardEntryCommand(
                    event.getUserId(),
                    event.getTotalPoints()
            );

            var updatedEntry = leaderboardCommandService.handle(command);

            if (updatedEntry.isPresent()) {
                logger.info("Updated leaderboard entry for user {} - Position: {}, Points: {}",
                        event.getUserId(),
                        updatedEntry.get().getPosition(),
                        updatedEntry.get().getTotalPoints());
            } else {
                logger.warn("Failed to update leaderboard entry for user {}", event.getUserId());
            }

        } catch (Exception e) {
            logger.error("Error handling CompetitiveProfileUpdatedEvent for user {}: {}",
                    event.getUserId(), e.getMessage(), e);
        }
    }
}
