package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.events;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Competitive Profile Created Event
 *
 * Domain event published when a new competitive profile is created.
 * This event enables other bounded contexts (like Leaderboard) to react
 * to competitive profile creation.
 *
 * This follows DDD principles for inter-context communication within
 * the monolithic application.
 */
@Getter
public class CompetitiveProfileCreatedEvent {

    private final String userId;
    private final Integer initialPoints;
    private final String initialRank;
    private final LocalDateTime occurredOn;

    /**
     * Constructor
     *
     * @param userId The user's unique identifier
     * @param initialPoints The initial points (usually 0 or fetched from Scores BC)
     * @param initialRank The initial rank (usually BRONZE)
     */
    public CompetitiveProfileCreatedEvent(String userId, Integer initialPoints, String initialRank) {
        this.userId = userId;
        this.initialPoints = initialPoints;
        this.initialRank = initialRank;
        this.occurredOn = LocalDateTime.now();
    }
}
