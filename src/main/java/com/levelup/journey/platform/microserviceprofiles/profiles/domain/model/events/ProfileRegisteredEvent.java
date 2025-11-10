package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.events;

import lombok.Getter;

import java.time.LocalDateTime;

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
    private final LocalDateTime occurredOn;

    /**
     * Constructor
     *
     * @param userId The user's unique identifier from IAM service (UUID)
     * @param profileId The profile's unique identifier from Profiles service (UUID)
     */
    public ProfileRegisteredEvent(String userId, String profileId) {
        this.userId = userId;
        this.profileId = profileId;
        this.occurredOn = LocalDateTime.now();
    }
}
