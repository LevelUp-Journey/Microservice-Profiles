package com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Create Competitive Profile Resource
 * DTO for creating a new competitive profile
 */
@Schema(description = "Request to create a new competitive profile")
public record CreateCompetitiveProfileResource(
        @NotBlank(message = "User ID is required")
        @Schema(description = "User identifier", example = "user123", requiredMode = Schema.RequiredMode.REQUIRED)
        String userId
) {
}
