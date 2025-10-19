package com.levelup.journey.platform.microserviceprofiles.scores.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.UUID;

/**
 * Score Resource
 * REST API resource for score data
 */
public record ScoreResource(
        @Schema(description = "Unique score identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        
        @Schema(description = "User ID who earned the score", example = "a6284ad2-8416-494c-b986-a4381e9de77b")
        String userId,
        
        @Schema(description = "Points earned", example = "100")
        Integer points,
        
        @Schema(description = "Source of the score", example = "CHALLENGE_COMPLETED")
        String source,
        
        @Schema(description = "Challenge ID if applicable", example = "challenge-001")
        String challengeId,
        
        @Schema(description = "Challenge type if applicable", example = "CODING_CHALLENGE")
        String challengeType,
        
        @Schema(description = "When the score was awarded")
        Date awardedAt
) {
}
