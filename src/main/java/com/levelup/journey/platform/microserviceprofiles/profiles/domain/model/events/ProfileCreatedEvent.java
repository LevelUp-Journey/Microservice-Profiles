package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.events;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Profile Created Event
 * Domain event published when a new profile is created in the system.
 * This event enables other bounded contexts to react to profile creation,
 * such as initializing competitive profiles, leaderboard entries, etc.
 *
 * Following DDD principles, this event facilitates loose coupling between
 * the Profiles BC and other bounded contexts (Competitive, Leaderboard).
 */
@Getter
public class ProfileCreatedEvent {

    private final String userId;
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String profileUrl;
    private final String provider;
    private final LocalDateTime occurredOn;

    /**
     * Constructor
     *
     * @param userId The user's unique identifier (UUID)
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @param username The generated or custom username
     * @param profileUrl The user's profile URL
     * @param provider The authentication provider
     */
    public ProfileCreatedEvent(String userId, String firstName, String lastName,
                              String username, String profileUrl, String provider) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.profileUrl = profileUrl;
        this.provider = provider;
        this.occurredOn = LocalDateTime.now();
    }
}
