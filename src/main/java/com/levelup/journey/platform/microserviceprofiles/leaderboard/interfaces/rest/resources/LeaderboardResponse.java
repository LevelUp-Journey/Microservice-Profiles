package com.levelup.journey.platform.microserviceprofiles.leaderboard.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Leaderboard Response
 * Wrapper for leaderboard entries with total user count
 */
@Schema(description = "Leaderboard response with entries and total user count")
public record LeaderboardResponse(
        @Schema(description = "List of leaderboard entries")
        List<LeaderboardEntryResource> entries,

        @Schema(description = "Total number of users in the leaderboard", example = "1500")
        Long totalUsers
) {}
