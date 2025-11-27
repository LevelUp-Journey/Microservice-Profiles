package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Profile Registered Event
 * Domain event published to Kafka when a new profile is created in the system.
 * This event is sent to the 'community-registration' topic to notify other microservices
 * about new user registrations, including the IAM userId and the generated profileId.
 *
 * This event enables loose coupling between microservices and supports event-driven
 * architecture for cross-service communication.
 */
@Getter
public class ProfileRegisteredEvent {

    private final String userId;
    private final String profileId;
    private final String username;
    private final String profileUrl;
    private final LocalDateTime occurredOn;

    /**
     * Constructor
     *
     * @param userId The user's unique identifier from IAM service (UUID)
     * @param profileId The profile's unique identifier from Profiles service (UUID)
     * @param username The username assigned to the profile
     * @param profileUrl The profile URL (can be null for local registrations)
     */
    public ProfileRegisteredEvent(String userId, String profileId, String username, String profileUrl) {
        this.userId = Objects.requireNonNull(userId, "userId is required");
        this.profileId = Objects.requireNonNull(profileId, "profileId is required");
        this.username = Objects.requireNonNull(username, "username is required");
        this.profileUrl = profileUrl; // Allow null for local registrations
        this.occurredOn = LocalDateTime.now();
    }
}
