package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Profile Updated Event
 * Published whenever an existing profile changes to keep external consumers in sync.
 */
@Getter
public class ProfileUpdatedEvent {

    private final String userId;
    private final String profileId;
    private final String username;
    private final String profileUrl;
    private final LocalDateTime occurredOn;

    public ProfileUpdatedEvent(String userId, String profileId, String username, String profileUrl) {
        this.userId = Objects.requireNonNull(userId, "userId is required");
        this.profileId = Objects.requireNonNull(profileId, "profileId is required");
        this.username = Objects.requireNonNull(username, "username is required");
        this.profileUrl = Objects.requireNonNull(profileUrl, "profileUrl is required");
        this.occurredOn = LocalDateTime.now();
    }
}
