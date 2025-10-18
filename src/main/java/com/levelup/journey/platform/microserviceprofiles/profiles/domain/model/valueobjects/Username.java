package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

@Embeddable
public record Username(String username) {

    // Username must be either USER + exactly 9 digits (13 characters total) or custom 3-15 chars
    private static final Pattern VALID_USERNAME_PATTERN = Pattern.compile("^(?:USER\\d{9}|[a-zA-Z0-9_.-]{3,15})$");
    private static final String INVALID_USERNAME_MESSAGE = "Username must be either auto-generated format (USER + 9 digits) or custom (3-15 alphanumeric characters, underscores, dots, or hyphens)";

    public Username() {
        this(null);
    }

    public Username {
        validateUsername(username);
    }

    /**
     * Validate username format - USER + 9 digits or custom 3-50 chars
     */
    private static void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        String trimmedUsername = username.trim();
        
        if (!VALID_USERNAME_PATTERN.matcher(trimmedUsername).matches()) {
            throw new IllegalArgumentException(INVALID_USERNAME_MESSAGE);
        }
    }

    @Override
    public String toString() {
        return username;
    }
}
