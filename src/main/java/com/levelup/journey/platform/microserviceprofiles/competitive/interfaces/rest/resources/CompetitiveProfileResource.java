package com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Competitive Profile Resource
 * DTO for competitive profile data transfer
 */
@Schema(description = "Competitive profile information including rank, points, and total time")
public record CompetitiveProfileResource(
        @Schema(description = "Profile unique identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "User identifier", example = "user123")
        String userId,

        @Schema(description = "Username", example = "john_doe")
        String username,

        @Schema(description = "Total accumulated points", example = "5420")
        Integer totalPoints,

        @Schema(description = "Current competitive rank", example = "DIAMOND")
        String currentRank,

        @Schema(description = "Next rank in progression", example = "MASTER", nullable = true)
        String nextRank,

        @Schema(description = "Points needed to reach next rank", example = "2080")
        Integer pointsNeededForNextRank,

        @Schema(description = "Leaderboard position", example = "42", nullable = true)
        Integer leaderboardPosition
) {
}
