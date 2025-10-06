package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources;

import java.util.UUID;

public record LeaderboardEntryResource(
    UUID profileId,
    String username,
    String fullName,
    String rankName,
    Integer currentScore,
    Integer position
) {
}