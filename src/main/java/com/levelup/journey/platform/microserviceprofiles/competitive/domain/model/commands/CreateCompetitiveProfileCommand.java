package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands;

/**
 * Create Competitive Profile Command
 * Command to create a new competitive profile for a user
 */
public record CreateCompetitiveProfileCommand(String userId) {

    public CreateCompetitiveProfileCommand {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
    }
}
