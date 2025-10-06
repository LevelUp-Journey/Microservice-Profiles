package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries;

import java.util.UUID;

public record GetProfileRankByProfileIdQuery(UUID profileId) {
    public GetProfileRankByProfileIdQuery {
        if (profileId == null) {
            throw new IllegalArgumentException("Profile ID cannot be null");
        }
    }
}