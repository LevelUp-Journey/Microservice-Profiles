package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * User Search Result Resource
 * DTO for user search results containing essential user information
 */
@Schema(description = "User search result with basic profile information")
public record UserSearchResultResource(
        @Schema(description = "User unique identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
        String userId,

        @Schema(description = "User's full name (firstName + lastName)", example = "John Doe")
        String fullName,

        @Schema(description = "User's profile picture URL", example = "https://example.com/profiles/user123.jpg")
        String profileUrl
) {
}
