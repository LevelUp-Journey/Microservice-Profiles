package com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Leaderboard Entry Resource
 * DTO for leaderboard entry display
 */
@Schema(description = "Single entry in the leaderboard")
public record LeaderboardEntryResource(
        @Schema(description = "Position in leaderboard", example = "1")
        Integer position,

        @Schema(description = "User identifier", example = "user123")
        String userId,

        @Schema(description = "Total points", example = "15000")
        Integer totalPoints,

        @Schema(description = "Competitive rank", example = "GRANDMASTER")
        String rank
) {
}
