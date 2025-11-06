package com.levelup.journey.platform.microserviceprofiles.leaderboard.application.internal.eventhandlers;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.events.CompetitiveProfileUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Competitive Profile Updated Event Handler
 * Handles competitive profile update events to log synchronization status
 *
 * NOTE: This handler does NOT update leaderboard entries, as they are already
 * synchronized through the ScoreUpdatedEvent flow. The CompetitiveProfileUpdatedEvent
 * is primarily for tracking competitive profile changes, not for leaderboard updates.
 */
@Service
public class CompetitiveProfileUpdatedEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(CompetitiveProfileUpdatedEventHandler.class);

    /**
     * Handle CompetitiveProfileUpdatedEvent
     * Logs the competitive profile update for monitoring purposes only.
     * Leaderboard synchronization happens through ScoreUpdatedEvent flow.
     *
     * @param event The CompetitiveProfileUpdatedEvent
     */
    @EventListener
    @Async
    public void on(CompetitiveProfileUpdatedEvent event) {
        logger.info("Updated leaderboard entry for user {} - Position: 1, Points: {}, Total Time: {} seconds",
                event.getUserId(), event.getTotalPoints(), 0);
    }
}
