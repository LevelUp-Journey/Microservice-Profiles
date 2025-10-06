package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.aggregates;

import com.levelup.journey.platform.microserviceprofiles.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "profile_ranks")
public class ProfileRank extends AuditableAbstractAggregateRoot<ProfileRank> {

    @Column(name = "profile_id", nullable = false)
    private UUID profileId;

    @Column(name = "rank_id", nullable = false)
    private UUID rankId;

    @Column(name = "current_score", nullable = false)
    private Integer currentScore;

    @Column(name = "total_score_accumulated", nullable = false)
    private Integer totalScoreAccumulated;

    protected ProfileRank() {
        // Default constructor for JPA
    }

    public ProfileRank(UUID profileId, UUID rankId) {
        this.profileId = profileId;
        this.rankId = rankId;
        this.currentScore = 1000; // Starting score
        this.totalScoreAccumulated = 1000;
    }

    public ProfileRank(UUID profileId, UUID rankId, Integer currentScore) {
        this.profileId = profileId;
        this.rankId = rankId;
        this.currentScore = currentScore;
        this.totalScoreAccumulated = currentScore;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public UUID getRankId() {
        return rankId;
    }

    public Integer getCurrentScore() {
        return currentScore;
    }

    public Integer getTotalScoreAccumulated() {
        return totalScoreAccumulated;
    }

    public void addScore(Integer points, String reason) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points to add must be positive");
        }
        this.currentScore += points;
        this.totalScoreAccumulated += points;
    }

    public void subtractScore(Integer points, String reason) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points to subtract must be positive");
        }
        this.currentScore = Math.max(0, this.currentScore - points);
    }

    public void updateRank(UUID newRankId) {
        this.rankId = newRankId;
    }

    public boolean hasMinimumScoreForRank(Integer requiredScore) {
        return this.currentScore >= requiredScore;
    }
}