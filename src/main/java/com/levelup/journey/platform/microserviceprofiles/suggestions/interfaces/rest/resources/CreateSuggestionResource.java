package com.levelup.journey.platform.microserviceprofiles.suggestions.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Create Suggestion Resource
 * DTO for creating a new suggestion
 */
@Schema(description = "Request to create a new suggestion")
public record CreateSuggestionResource(
        @NotBlank(message = "Comment is required")
        @Schema(description = "Suggestion comment", example = "This is a suggestion", requiredMode = Schema.RequiredMode.REQUIRED)
        String comment
) {
}
