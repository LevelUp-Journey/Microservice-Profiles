package com.levelup.journey.platform.microserviceprofiles.competitive.application.internal.eventhandlers;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.SeedRanksCommand;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.services.RankCommandService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * Application Ready Event Handler
 * Handles initial data seeding when application is fully started and ready
 */
@Service
public class ApplicationReadyEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationReadyEventHandler.class);
    private final RankCommandService rankCommandService;

    @PersistenceContext
    private EntityManager entityManager;

    public ApplicationReadyEventHandler(RankCommandService rankCommandService) {
        this.rankCommandService = rankCommandService;
    }

    /**
     * Handle application ready event
     * Seeds rank reference data when application starts
     * 
     * @param event The application ready event
     */
    @EventListener
    @Transactional
    public void on(ApplicationReadyEvent event) {
        var applicationName = event.getApplicationContext().getId();
        logger.info("Starting to verify if ranks seeding is needed for {} at {}", 
            applicationName, currentTimestamp());
        
        // Drop any constraint that might cause issues during seeding
        try {
            entityManager.createNativeQuery("ALTER TABLE ranks DROP CONSTRAINT IF EXISTS ranks_immutable_check")
                .executeUpdate();
            logger.info("Dropped ranks_immutable_check constraint if it existed");
        } catch (Exception e) {
            logger.warn("Could not drop ranks constraint: {}", e.getMessage());
        }
        
        var seedRanksCommand = new SeedRanksCommand();
        rankCommandService.handle(seedRanksCommand);
        
        logger.info("Ranks seeding verification finished for {} at {}", 
            applicationName, currentTimestamp());
    }

    private Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
