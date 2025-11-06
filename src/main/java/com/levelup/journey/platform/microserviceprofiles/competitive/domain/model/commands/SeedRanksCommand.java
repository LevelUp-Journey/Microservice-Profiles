package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands;

/**
 * Seed Ranks Command
 * Command to initialize rank reference data in the database
 */
public record SeedRanksCommand() {
    // No parameters needed - seeds all ranks from CompetitiveRank enum
}
