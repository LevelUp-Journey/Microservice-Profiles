package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources;

import java.util.Date;
import java.util.UUID;

public record ProfileRankResource(
    UUID id,
    UUID profileId,
    UUID rankId,
    String rankName,
    Integer currentScore,
    Integer totalScoreAccumulated,
    Date createdAt,
    Date updatedAt
) {
}