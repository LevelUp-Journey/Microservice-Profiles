package com.levelup.journey.platform.microserviceprofiles.suggestions.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Suggestion Resource
 * DTO for suggestion response
 */
@Schema(description = "Suggestion details")
public record SuggestionResource(
        @Schema(description = "Suggestion identifier", example = "1")
        Long id,

        @Schema(description = "Suggestion comment", example = "This is a suggestion")
        String comment,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Resolution status", example = "false")
        Boolean isResolved
) {
}
