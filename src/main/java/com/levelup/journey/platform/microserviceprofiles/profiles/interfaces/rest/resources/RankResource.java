package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources;

import java.util.UUID;

public record RankResource(
    UUID id,
    String name,
    Integer minScore,
    Integer maxScore,
    Integer rankOrder
) {
}