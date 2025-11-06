package com.levelup.journey.platform.microserviceprofiles.competitive.application.internal.eventhandlers;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.SeedRanksCommand;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.services.CompetitiveProfileCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Application Ready Event Handler
 * Handles initial data seeding when application is fully started and ready
 * Seeds competitive ranks into the database
 */
@Service
public class ApplicationReadyEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationReadyEventHandler.class);
    private final CompetitiveProfileCommandService competitiveProfileCommandService;

    public ApplicationReadyEventHandler(CompetitiveProfileCommandService competitiveProfileCommandService) {
        this.competitiveProfileCommandService = competitiveProfileCommandService;
    }

    /**
     * Handle application ready event
     * Seeds rank reference data when application starts
     *
     * @param event The application ready event
     */
    @EventListener
    public void on(ApplicationReadyEvent event) {
        var applicationName = event.getApplicationContext().getId();
        logger.info("Application {} is ready. Starting rank seeding verification.", applicationName);

        try {
            var seedRanksCommand = new SeedRanksCommand();
            competitiveProfileCommandService.handle(seedRanksCommand);
            logger.info("Rank seeding verification completed for {}", applicationName);
        } catch (Exception e) {
            logger.error("Error during rank seeding for {}: {}", applicationName, e.getMessage(), e);
        }
    }
}
