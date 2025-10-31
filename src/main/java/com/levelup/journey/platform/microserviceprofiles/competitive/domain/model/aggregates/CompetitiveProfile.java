package com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.aggregates;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.CreateCompetitiveProfileCommand;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.UpdateCompetitivePointsCommand;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.entities.Rank;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.CompetitiveUserId;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rank_id", nullable = false)
    private Rank currentRank;

    protected CompetitiveProfile() {
        // JPA constructor
    }

    /**
     * Constructor from CreateCompetitiveProfileCommand
     * Initializes profile with zero points and BRONZE rank
     *
     * @param command The creation command
     * @param bronzeRank The bronze rank entity from database
     */
    public CompetitiveProfile(CreateCompetitiveProfileCommand command, Rank bronzeRank) {
        this.userId = new CompetitiveUserId(command.userId());
        this.totalPoints = TotalPoints.zero();
        this.currentRank = bronzeRank;
    }

    /**
     * Constructor with initial points (for sync from Scores BC)
     *
     * @param userId User identifier
     * @param initialPoints Initial points from Scores BC
     * @param initialRank The initial rank entity based on points
     */
    public CompetitiveProfile(String userId, Integer initialPoints, Rank initialRank) {
        this.userId = new CompetitiveUserId(userId);
        this.totalPoints = new TotalPoints(initialPoints);
        this.currentRank = initialRank;
    }

    /**
     * Update total points and recalculate rank
     *
     * @param command Update command with new total points
     * @param newRank The new rank entity based on points
     */
    public void updatePoints(UpdateCompetitivePointsCommand command, Rank newRank) {
        this.totalPoints = new TotalPoints(command.newTotalPoints());
        this.currentRank = newRank;
    }

    /**
     * Update total points from external source (Scores BC)
     *
     * @param newPoints New total points value
     * @param newRank The new rank entity based on points
     */
    public void syncPointsFromScores(Integer newPoints, Rank newRank) {
        this.totalPoints = new TotalPoints(newPoints);
        this.currentRank = newRank;
    }

    /**
     * Update rank entity
     *
     * @param rank The new rank entity
     */
    public void updateRank(Rank rank) {
        this.currentRank = rank;
    }

    // Getters

    public String getUserId() {
        return userId.userId();
    }

    public Integer getTotalPoints() {
        return totalPoints.value();
    }

    public Rank getCurrentRank() {
        return currentRank;
    }

    public String getCurrentRankName() {
        return currentRank.getRankName().name();
    }
}
