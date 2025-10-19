package com.levelup.journey.platform.microserviceprofiles.scores.domain.model.events;

import java.time.LocalDateTime;

/**
 * Domain event published when points are awarded to a user
 * This event is consumed by Leaderboard and Competitive Level contexts
 */
public class ScorePointsAwardedEvent {
    private final String userId;
    private final Integer points;
    private final String source;
    private final String challengeId;
    private final LocalDateTime awardedAt;

    public ScorePointsAwardedEvent(String userId, Integer points, String source, String challengeId) {
        this.userId = userId;
        this.points = points;
        this.source = source;
        this.challengeId = challengeId;
        this.awardedAt = LocalDateTime.now();
    }

    public String getUserId() {
        return userId;
    }

    public Integer getPoints() {
        return points;
    }

    public String getSource() {
        return source;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public LocalDateTime getAwardedAt() {
        return awardedAt;
    }
}
