package com.levelup.journey.platform.microserviceprofiles.scores.domain.model.aggregates;

import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.commands.RecordScoreFromChallengeCommand;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.valueobjects.ChallengeId;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.valueobjects.Points;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.valueobjects.ScoreSource;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.valueobjects.ScoreUserId;
import com.levelup.journey.platform.microserviceprofiles.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;

/**
 * Score Aggregate Root
 * Represents a single score record awarded to a user
 */
@Entity
@Table(name = "scores")
public class Score extends AuditableAbstractAggregateRoot<Score> {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "user_id", nullable = false))
    })
    private ScoreUserId userId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "points", nullable = false))
    })
    private Points points;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 50)
    private ScoreSource source;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "challengeId", column = @Column(name = "challenge_id", length = 100))
    })
    private ChallengeId challengeId;

    @Column(name = "challenge_type", length = 50)
    private String challengeType;

    protected Score() {
        // JPA constructor
    }

    /**
     * Constructor from RecordScoreFromChallengeCommand
     */
    public Score(RecordScoreFromChallengeCommand command) {
        this.userId = new ScoreUserId(command.userId());
        this.points = new Points(command.points());
        this.source = ScoreSource.CHALLENGE_COMPLETED;
        this.challengeId = new ChallengeId(command.challengeId());
        this.challengeType = command.challengeType();
    }

    // Getters
    public String getUserId() {
        return userId.userId();
    }

    public Integer getPoints() {
        return points.value();
    }

    public ScoreSource getSource() {
        return source;
    }

    public String getChallengeId() {
        return challengeId != null ? challengeId.challengeId() : null;
    }

    public String getChallengeType() {
        return challengeType;
    }
}
