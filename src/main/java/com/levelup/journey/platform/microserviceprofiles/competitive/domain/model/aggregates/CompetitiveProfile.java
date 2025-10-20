package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.aggregates;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.CreateCompetitiveProfileCommand;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.UpdateCompetitivePointsCommand;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.CompetitiveRank;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.CompetitiveUserId;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.LeaderboardPosition;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.TotalPoints;
import com.levelup.journey.platform.microserviceprofiles.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;

/**
 * Competitive Profile Aggregate Root
 * Represents a user's competitive standing in the platform
 */
@Entity
@Table(name = "competitive_profiles")
public class CompetitiveProfile extends AuditableAbstractAggregateRoot<CompetitiveProfile> {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "user_id", nullable = false, unique = true))
    })
    private CompetitiveUserId userId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "total_points", nullable = false))
    })
    private TotalPoints totalPoints;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_rank", nullable = false)
    private CompetitiveRank currentRank;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "position", column = @Column(name = "leaderboard_position"))
    })
    private LeaderboardPosition leaderboardPosition;

    protected CompetitiveProfile() {
        // JPA constructor
    }

    /**
     * Constructor from CreateCompetitiveProfileCommand
     * Initializes profile with zero points and BRONZE rank
     *
     * @param command The creation command
     */
    public CompetitiveProfile(CreateCompetitiveProfileCommand command) {
        this.userId = new CompetitiveUserId(command.userId());
        this.totalPoints = TotalPoints.zero();
        this.currentRank = CompetitiveRank.BRONZE;
        this.leaderboardPosition = null; // Will be set during leaderboard calculation
    }

    /**
     * Constructor with initial points (for sync from Scores BC)
     *
     * @param userId User identifier
     * @param initialPoints Initial points from Scores BC
     */
    public CompetitiveProfile(String userId, Integer initialPoints) {
        this.userId = new CompetitiveUserId(userId);
        this.totalPoints = new TotalPoints(initialPoints);
        this.currentRank = CompetitiveRank.fromPoints(initialPoints);
        this.leaderboardPosition = null;
    }

    /**
     * Update total points and recalculate rank
     *
     * @param command Update command with new total points
     */
    public void updatePoints(UpdateCompetitivePointsCommand command) {
        this.totalPoints = new TotalPoints(command.newTotalPoints());
        this.currentRank = CompetitiveRank.fromPoints(command.newTotalPoints());
    }

    /**
     * Update total points from external source (Scores BC)
     *
     * @param newPoints New total points value
     */
    public void syncPointsFromScores(Integer newPoints) {
        this.totalPoints = new TotalPoints(newPoints);
        this.currentRank = CompetitiveRank.fromPoints(newPoints);
    }

    /**
     * Update leaderboard position and potentially grant TOP500 rank
     *
     * @param position New leaderboard position
     */
    public void updateLeaderboardPosition(LeaderboardPosition position) {
        this.leaderboardPosition = position;

        // Grant TOP500 rank if position qualifies
        if (position.isTop500()) {
            this.currentRank = CompetitiveRank.TOP500;
        }
    }

    /**
     * Update rank
     *
     * @param rank The new rank
     */
    public void updateRank(CompetitiveRank rank) {
        this.currentRank = rank;
    }

    /**
     * Check if profile qualifies for TOP500
     *
     * @return true if current rank is TOP500
     */
    public boolean isTop500() {
        return this.currentRank == CompetitiveRank.TOP500;
    }

    // Getters

    public String getUserId() {
        return userId.userId();
    }

    public Integer getTotalPoints() {
        return totalPoints.value();
    }

    public CompetitiveRank getCurrentRank() {
        return currentRank;
    }

    public String getCurrentRankName() {
        return currentRank.name();
    }

    public Integer getLeaderboardPosition() {
        return leaderboardPosition != null ? leaderboardPosition.position() : null;
    }
}
