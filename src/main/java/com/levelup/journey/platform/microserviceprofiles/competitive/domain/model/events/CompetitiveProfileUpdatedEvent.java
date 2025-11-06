package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.events;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Competitive Profile Updated Event
 * Domain event published when a competitive profile's points or rank are updated
 * Used for inter-context communication within the monolith
 */
@Getter
public class CompetitiveProfileUpdatedEvent {

    private final String userId;
    private final Integer totalPoints;
    private final String rankName;
    private final LocalDateTime occurredOn;

    public CompetitiveProfileUpdatedEvent(String userId, Integer totalPoints, String rankName) {
        this.userId = userId;
        this.totalPoints = totalPoints;
        this.rankName = rankName;
        this.occurredOn = LocalDateTime.now();
    }
}
