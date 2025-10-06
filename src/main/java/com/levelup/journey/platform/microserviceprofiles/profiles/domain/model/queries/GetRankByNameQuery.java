package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries;

public record GetRankByNameQuery(String rankName) {
    public GetRankByNameQuery {
        if (rankName == null || rankName.isBlank()) {
            throw new IllegalArgumentException("Rank name cannot be null or blank");
        }
    }
}