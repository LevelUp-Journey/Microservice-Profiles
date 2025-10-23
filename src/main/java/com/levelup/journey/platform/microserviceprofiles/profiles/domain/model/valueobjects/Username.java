package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

@Embeddable
public record Username(String username) {

    // Username: alphanumeric characters only, no spaces, max 30 characters
    private static final Pattern VALID_USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]{1,30}$");
    private static final String INVALID_USERNAME_MESSAGE = "Username must contain only alphanumeric characters (letters and numbers), no spaces, maximum 30 characters";

    public Username() {
        this(null);
    }

    public Username {
        validateUsername(username);
    }

    /**
     * Validate username format - alphanumeric only, no spaces, max 30 characters
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
