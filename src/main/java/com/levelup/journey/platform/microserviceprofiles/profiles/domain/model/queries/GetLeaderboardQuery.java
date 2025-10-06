package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries;

public record GetLeaderboardQuery(Integer limit) {
    public GetLeaderboardQuery {
        if (limit == null || limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        if (limit > 100) {
            throw new IllegalArgumentException("Limit cannot exceed 100");
        }
    }

    public GetLeaderboardQuery() {
        this(10); // Default limit
    }
}