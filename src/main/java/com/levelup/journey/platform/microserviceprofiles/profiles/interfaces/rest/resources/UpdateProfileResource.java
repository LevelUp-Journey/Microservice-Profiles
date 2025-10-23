package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

/**
 * Resource for updating a profile.
 */
public record UpdateProfileResource(
        @Pattern(regexp = "^[A-Za-zÁáÉéÍíÓóÚúÑñÜü\\s\\-]*$",
                 message = "First name must contain only letters, accents, spaces, and hyphens")
        @Size(max = 20, message = "First name cannot exceed 20 characters")
        @Schema(description = "User's first name (supports international characters)", example = "María")
        String firstName,

        @Pattern(regexp = "^[A-Za-zÁáÉéÍíÓóÚúÑñÜü\\s\\-]*$",
                 message = "Last name must contain only letters, accents, spaces, and hyphens")
        @Size(max = 20, message = "Last name cannot exceed 20 characters")
                @Schema(description = "User's last name (supports international characters)", example = "González")
        String lastName,

        @Pattern(regexp = "^[a-zA-Z0-9]{1,30}$",
                 message = "Username must contain only alphanumeric characters (letters and numbers), no spaces, maximum 30 characters")
        @Schema(description = "Username (alphanumeric only, no spaces, max 30 chars)", example = "mariadev123")
        String username,

        @URL(message = "Profile URL must be a valid HTTP or HTTPS URL")
        @Size(max = 255, message = "Profile URL cannot exceed 255 characters")
        @Schema(description = "User's profile URL (optional)", example = "https://github.com/username")
        String profileUrl
) {
}
