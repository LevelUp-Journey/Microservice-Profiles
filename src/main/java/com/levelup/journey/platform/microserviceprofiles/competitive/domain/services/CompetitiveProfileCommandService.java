package com.levelup.journey.platform.microserviceprofiles.competitive.domain.services;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.aggregates.CompetitiveProfile;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.CreateCompetitiveProfileCommand;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.SyncCompetitiveProfileFromScoresCommand;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.UpdateCompetitivePointsCommand;

import java.util.Optional;

/**
 * Competitive Profile Command Service
 * Handles commands related to competitive profile operations
 */
public interface CompetitiveProfileCommandService {

    /**
     * Handle Create Competitive Profile Command
     * Creates a new competitive profile for a user
     *
     * @param command The {@link CreateCompetitiveProfileCommand}
     * @return Optional of created {@link CompetitiveProfile}
     */
    Optional<CompetitiveProfile> handle(CreateCompetitiveProfileCommand command);

    /**
     * Handle Update Competitive Points Command
     * Updates a user's points and recalculates their rank
     *
     * @param command The {@link UpdateCompetitivePointsCommand}
     * @return Optional of updated {@link CompetitiveProfile}
     */
    Optional<CompetitiveProfile> handle(UpdateCompetitivePointsCommand command);

    /**
     * Handle Sync Competitive Profile From Scores Command
     * Synchronizes competitive profile with current scores from Scores BC
     *
     * @param command The {@link SyncCompetitiveProfileFromScoresCommand}
     * @return Optional of synchronized {@link CompetitiveProfile}
     */
    Optional<CompetitiveProfile> handle(SyncCompetitiveProfileFromScoresCommand command);
}
