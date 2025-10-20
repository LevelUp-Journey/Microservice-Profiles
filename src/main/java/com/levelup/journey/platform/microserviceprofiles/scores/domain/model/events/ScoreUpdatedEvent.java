package com.levelup.journey.platform.microserviceprofiles.scores.domain.model.events;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Score Updated Event
 * Domain event published when a user's score is created or updated
 * Used for inter-context communication within the monolith
 */
@Getter
public class ScoreUpdatedEvent {
    
    private final String userId;
    private final Integer newTotalPoints;
    private final Integer pointsEarned;
    private final String source;
    private final LocalDateTime occurredOn;

    public ScoreUpdatedEvent(String userId, Integer newTotalPoints, Integer pointsEarned, String source) {
        this.userId = userId;
        this.newTotalPoints = newTotalPoints;
        this.pointsEarned = pointsEarned;
        this.source = source;
        this.occurredOn = LocalDateTime.now();
    }
}
