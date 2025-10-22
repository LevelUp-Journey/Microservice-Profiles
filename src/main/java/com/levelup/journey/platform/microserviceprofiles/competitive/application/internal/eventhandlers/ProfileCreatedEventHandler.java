package com.levelup.journey.platform.microserviceprofiles.competitive.application.internal.eventhandlers;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.CreateCompetitiveProfileCommand;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.services.CompetitiveProfileCommandService;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.events.ProfileCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Profile Created Event Handler
 *
 * Listens for profile creation events and automatically initializes
 * a competitive profile for the new user. This ensures that the competitive
 * profile exists before any score events arrive from Kafka.
 *
 * This implements the DDD pattern where the Competitive BC reacts to
 * domain events from the Profiles BC, maintaining loose coupling between
 * bounded contexts.
 *
 * Flow:
 * 1. User registers (IAM) → Kafka event
 * 2. Profile created (Profiles BC) → ProfileCreatedEvent published
 * 3. This handler creates CompetitiveProfile with 0 points and BRONZE rank
 * 4. Later, when score events arrive, the profile already exists and can be updated
 */
@Service
public class ProfileCreatedEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProfileCreatedEventHandler.class);

    private final CompetitiveProfileCommandService competitiveProfileCommandService;

    public ProfileCreatedEventHandler(CompetitiveProfileCommandService competitiveProfileCommandService) {
        this.competitiveProfileCommandService = competitiveProfileCommandService;
    }

    /**
     * Handles profile created events by initializing a competitive profile.
     *
     * Uses @TransactionalEventListener to execute AFTER the profile creation
     * transaction commits, ensuring the profile is persisted before creating
     * the competitive profile.
     *
     * Uses @Async to avoid blocking the profile creation process.
     *
     * The competitive profile is created with:
     * - 0 initial points (or fetched from Scores BC if available)
     * - BRONZE rank (default starting rank)
     *
     * @param event the profile created event containing user details
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ProfileCreatedEvent event) {
        logger.info("Received ProfileCreatedEvent for user: {} (username: {})",
            event.getUserId(), event.getUsername());

        try {
            var command = new CreateCompetitiveProfileCommand(event.getUserId());
            var competitiveProfile = competitiveProfileCommandService.handle(command);

            if (competitiveProfile.isPresent()) {
                logger.info("Successfully initialized competitive profile for user: {} with {} points and rank {}",
                    event.getUserId(),
                    competitiveProfile.get().getTotalPoints(),
                    competitiveProfile.get().getCurrentRankName());
            } else {
                logger.warn("Failed to initialize competitive profile for user: {}", event.getUserId());
            }
        } catch (Exception e) {
            logger.error("Error initializing competitive profile for user: {}",
                event.getUserId(), e);
        }
    }
}
