package com.levelup.journey.platform.microserviceprofiles.competitive.domain.services;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.SeedRanksCommand;

/**
 * Rank Command Service
 * Service interface for rank-related commands
 */
public interface RankCommandService {
    
    /**
     * Handle seed ranks command
     * Seeds all ranks from CompetitiveRank enum into the database
     * 
     * @param command The seed ranks command
     */
    void handle(SeedRanksCommand command);
}
