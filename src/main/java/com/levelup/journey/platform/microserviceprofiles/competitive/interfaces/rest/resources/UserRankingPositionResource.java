package com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * User Ranking Position Resource
 * DTO for user's ranking position information
 */
@Schema(description = "User's position in the global leaderboard")
public record UserRankingPositionResource(
        @Schema(description = "User identifier", example = "user123")
        String userId,

        @Schema(description = "Global leaderboard position", example = "42")
        Integer position,

        @Schema(description = "Total points", example = "5420")
        Integer totalPoints,

        @Schema(description = "Current rank", example = "DIAMOND")
        String currentRank
) {
}
