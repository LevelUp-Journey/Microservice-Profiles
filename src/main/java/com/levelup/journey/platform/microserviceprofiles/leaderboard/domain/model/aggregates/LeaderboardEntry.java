package com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.aggregates;

import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.commands.UpdateLeaderboardEntryCommand;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.valueobjects.LeaderboardPoints;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.valueobjects.LeaderboardPosition;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.valueobjects.LeaderboardUserId;
import com.levelup.journey.platform.microserviceprofiles.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;

/**
 * Leaderboard Entry Aggregate Root
 * Represents a user's entry in the global leaderboard
 */
@Entity
@Table(name = "leaderboard_entries")
public class LeaderboardEntry extends AuditableAbstractAggregateRoot<LeaderboardEntry> {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "user_id", nullable = false, unique = true))
    })
    private LeaderboardUserId userId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "points", column = @Column(name = "total_points", nullable = false))
    })
    private LeaderboardPoints totalPoints;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "position", column = @Column(name = "leaderboard_position", nullable = false))
    })
    private LeaderboardPosition position;

    protected LeaderboardEntry() {
        // JPA constructor
    }

    /**
     * Constructor for creating a new leaderboard entry
     *
     * @param command The UpdateLeaderboardEntryCommand
     * @param calculatedPosition The calculated position based on points
     */
    public LeaderboardEntry(UpdateLeaderboardEntryCommand command, Integer calculatedPosition) {
        this.userId = new LeaderboardUserId(command.userId());
        this.totalPoints = new LeaderboardPoints(command.totalPoints());
        this.position = new LeaderboardPosition(calculatedPosition);
    }

    /**
     * Constructor with explicit values
     *
     * @param userId User identifier
     * @param totalPoints Total points
     * @param position Leaderboard position
     */
    public LeaderboardEntry(String userId, Integer totalPoints, Integer position) {
        this.userId = new LeaderboardUserId(userId);
        this.totalPoints = new LeaderboardPoints(totalPoints);
        this.position = new LeaderboardPosition(position);
    }

    /**
     * Update the entry with new points and recalculate position
     *
     * @param newPoints New total points
     * @param newPosition New calculated position
     */
    public void updatePointsAndPosition(Integer newPoints, Integer newPosition) {
        this.totalPoints = new LeaderboardPoints(newPoints);
        this.position = new LeaderboardPosition(newPosition);
    }

    /**
     * Update only the position (used during recalculation)
     *
     * @param newPosition New calculated position
     */
    public void updatePosition(Integer newPosition) {
        this.position = new LeaderboardPosition(newPosition);
    }

    // Getters

    public String getUserId() {
        return userId.userId();
    }

    public Integer getTotalPoints() {
        return totalPoints.points();
    }

    public Integer getPosition() {
        return position.position();
    }

    public boolean isTop500() {
        return position.isTop500();
    }
}
