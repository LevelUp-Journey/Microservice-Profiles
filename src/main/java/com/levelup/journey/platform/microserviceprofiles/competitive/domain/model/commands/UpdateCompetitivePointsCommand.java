package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands;

/**
 * Update Competitive Points Command
 * Command to update a user's competitive points and recalculate rank
 */
public record UpdateCompetitivePointsCommand(String userId, Integer newTotalPoints) {

    public UpdateCompetitivePointsCommand {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
        if (newTotalPoints == null || newTotalPoints < 0) {
            throw new IllegalArgumentException("Total points cannot be null or negative");
        }
    }
}
