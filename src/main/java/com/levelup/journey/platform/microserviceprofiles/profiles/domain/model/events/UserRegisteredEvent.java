package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.events;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Domain event representing a user registration from IAM context
 * This event is received from Kafka topic: iam.user.registered
 */
@Getter
public class UserRegisteredEvent {
    private final String userId;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String profileUrl;
    private final String provider;
    private final LocalDateTime registeredAt;
    private final LocalDateTime occurredOn;

    public UserRegisteredEvent(String userId, String email, String firstName, String lastName,
                               String profileUrl, String provider, LocalDateTime registeredAt) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileUrl = profileUrl;
        this.provider = provider;
        this.registeredAt = registeredAt;
        this.occurredOn = LocalDateTime.now();
    }
}
