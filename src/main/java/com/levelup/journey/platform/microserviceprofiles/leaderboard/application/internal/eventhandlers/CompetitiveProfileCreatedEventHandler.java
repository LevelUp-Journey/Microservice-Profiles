package com.levelup.journey.platform.microserviceprofiles.leaderboard.application.internal.eventhandlers;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.events.CompetitiveProfileCreatedEvent;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.commands.UpdateLeaderboardEntryCommand;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.services.LeaderboardCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Competitive Profile Created Event Handler
 *
 * Listens for competitive profile creation events and automatically
 * initializes a leaderboard entry for the new user.
 *
 * This implements the DDD pattern where the Leaderboard BC reacts to
 * domain events from the Competitive BC, maintaining loose coupling
 * between bounded contexts.
 *
 * Flow:
 * 1. Profile created → ProfileCreatedEvent
 * 2. CompetitiveProfile created → CompetitiveProfileCreatedEvent
 * 3. This handler creates LeaderboardEntry with initial points and position
 */
@Service
public class CompetitiveProfileCreatedEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(CompetitiveProfileCreatedEventHandler.class);

    private final LeaderboardCommandService leaderboardCommandService;

    public CompetitiveProfileCreatedEventHandler(LeaderboardCommandService leaderboardCommandService) {
        this.leaderboardCommandService = leaderboardCommandService;
    }

    /**
     * Handles competitive profile created events by initializing a leaderboard entry.
     *
     * Uses @TransactionalEventListener to execute AFTER the competitive profile
     * creation transaction commits, ensuring the profile is persisted before
     * creating the leaderboard entry.
     *
     * Uses @Async to avoid blocking the competitive profile creation process.
     *
     * The leaderboard entry is created with:
     * - Initial points from the competitive profile (usually 0)
     * - Calculated position based on points
     *
     * @param event the competitive profile created event containing user details
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(CompetitiveProfileCreatedEvent event) {
        logger.info("Received CompetitiveProfileCreatedEvent for user: {} with {} points",
            event.getUserId(), event.getInitialPoints());

        try {
            var command = new UpdateLeaderboardEntryCommand(
                event.getUserId(),
                event.getInitialPoints()
            );
            var leaderboardEntry = leaderboardCommandService.handle(command);

            if (leaderboardEntry.isPresent()) {
                logger.info("Successfully initialized leaderboard entry for user: {} at position {}",
                    event.getUserId(), leaderboardEntry.get().getPosition());
            } else {
                logger.warn("Failed to initialize leaderboard entry for user: {}", event.getUserId());
            }
        } catch (Exception e) {
            logger.error("Error initializing leaderboard entry for user: {}",
                event.getUserId(), e);
        }
    }
}
