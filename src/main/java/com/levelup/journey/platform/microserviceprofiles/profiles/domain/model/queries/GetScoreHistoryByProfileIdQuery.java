package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries;

import java.util.UUID;

public record GetScoreHistoryByProfileIdQuery(UUID profileId) {
    public GetScoreHistoryByProfileIdQuery {
        if (profileId == null) {
            throw new IllegalArgumentException("Profile ID cannot be null");
        }
    }
}