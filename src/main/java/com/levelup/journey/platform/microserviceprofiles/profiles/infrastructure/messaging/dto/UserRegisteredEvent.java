package com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.messaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for User Registered Event from IAM Service
 * Represents the event published when a new user is registered in the system
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegisteredEvent {
    private String userId;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String profileUrl;
    private String provider;
    private Instant timestamp;
}
