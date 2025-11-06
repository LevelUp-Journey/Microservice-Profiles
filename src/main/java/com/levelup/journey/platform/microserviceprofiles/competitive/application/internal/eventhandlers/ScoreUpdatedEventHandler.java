package com.levelup.journey.platform.microserviceprofiles.competitive.application.internal.eventhandlers;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.SyncCompetitiveProfileFromScoresCommand;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.services.CompetitiveProfileCommandService;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.events.ScoreUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Event handler that listens for score updates and synchronizes competitive profiles.
 * This implements the ACL pattern where Competitive BC consumes events from Scores BC.
 */
@Service("competitiveScoreUpdatedEventHandler")
public class ScoreUpdatedEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(ScoreUpdatedEventHandler.class);
    
    private final CompetitiveProfileCommandService competitiveProfileCommandService;

    public ScoreUpdatedEventHandler(CompetitiveProfileCommandService competitiveProfileCommandService) {
        this.competitiveProfileCommandService = competitiveProfileCommandService;
    }

    /**
     * Handles score updated events by synchronizing the competitive profile.
     * Uses @Async to avoid blocking the score recording process.
     * 
     * @param event the score updated event containing user ID and new total points
     */
    @Async
    @EventListener
    public void on(ScoreUpdatedEvent event) {
        logger.info("Received ScoreUpdatedEvent for user: {} with total points: {}", 
            event.getUserId(), event.getNewTotalPoints());
        
        try {
            var command = new SyncCompetitiveProfileFromScoresCommand(event.getUserId());
            competitiveProfileCommandService.handle(command);
            
            logger.info("Successfully synchronized competitive profile for user: {}", event.getUserId());
        } catch (Exception e) {
            logger.error("Failed to synchronize competitive profile for user: {}", 
                event.getUserId(), e);
        }
    }
}
