package com.levelup.journey.platform.microserviceprofiles.leaderboard.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Leaderboard Entry Resource
 * DTO for leaderboard entry data transfer
 */
@Schema(description = "Leaderboard entry with user position, points, and cumulative execution time")
public record LeaderboardEntryResource(
        @Schema(description = "Entry unique identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "User identifier", example = "user123")
        String userId,

        @Schema(description = "Total points", example = "5420")
        Integer totalPoints,

        @Schema(description = "Leaderboard position", example = "42")
        Integer position,

        @Schema(description = "Total cumulative time to complete all challenges in seconds", example = "86400")
        Long totalTimeToAchievePointsMs,

        @Schema(description = "Whether user is in TOP500", example = "true")
        Boolean isTop500
) {
}
