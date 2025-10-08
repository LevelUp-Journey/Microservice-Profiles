package com.levelup.journey.platform.microserviceprofiles.shared.infrastructure.startup;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.InitializeRanksCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.RankCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Application Startup Initializer
 * Initializes application data on startup
 */
@Component
public class ApplicationStartupInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupInitializer.class);

    private final RankCommandService rankCommandService;

    public ApplicationStartupInitializer(RankCommandService rankCommandService) {
        this.rankCommandService = rankCommandService;
    }

    /**
     * Initialize application data when the application is ready
     * This ensures ranks are created before any user profiles are processed
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("Initializing application data on startup...");

        try {
            // Initialize ranks (Bronze, Silver, Gold, Platinum, Diamond)
            var command = new InitializeRanksCommand();
            rankCommandService.handle(command);
            logger.info("Successfully initialized ranks system");
        } catch (IllegalStateException e) {
            logger.info("Ranks already initialized: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to initialize ranks: {}", e.getMessage(), e);
        }

        logger.info("Application startup initialization completed");
    }
}
