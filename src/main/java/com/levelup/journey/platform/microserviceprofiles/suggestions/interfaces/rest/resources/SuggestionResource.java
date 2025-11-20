package com.levelup.journey.platform.microserviceprofiles.suggestions.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.UUID;

/**
 * Suggestion Resource
 * DTO for suggestion response
 */
@Schema(description = "Suggestion details")
public record SuggestionResource(
        @Schema(description = "Suggestion identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Suggestion comment", example = "This is a suggestion")
        String comment,

        @Schema(description = "Creation timestamp")
        Date createdAt,

        @Schema(description = "Resolution status", example = "false")
        Boolean isResolved
) {
}
