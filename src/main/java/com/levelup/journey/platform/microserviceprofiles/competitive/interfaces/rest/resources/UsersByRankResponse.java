package com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Users By Rank Response
 * Wrapper for competitive profiles by rank with total user count
 */
@Schema(description = "Response containing users of a specific rank with total count")
public record UsersByRankResponse(
        @Schema(description = "List of competitive profiles with the specified rank")
        List<CompetitiveProfileResource> profiles,

        @Schema(description = "Total number of users with this rank", example = "250")
        Long totalUsers
) {
}
