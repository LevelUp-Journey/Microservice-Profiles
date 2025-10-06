package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.entities;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.Score;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.ScoreChangeReason;
import com.levelup.journey.platform.microserviceprofiles.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "score_audit_logs")
public class ScoreAuditLog extends AuditableModel {

    @Column(name = "profile_id", nullable = false)
    private UUID profileId;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "score_change", nullable = false))
    })
    private Score scoreChange;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "previous_score", nullable = false))
    })
    private Score previousScore;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "new_score", nullable = false))
    })
    private Score newScore;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "reason", column = @Column(name = "change_reason", nullable = false))
    })
    private ScoreChangeReason changeReason;

    @Column(name = "change_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ScoreChangeType changeType;

    @Column(name = "external_reference_id")
    private String externalReferenceId;

    protected ScoreAuditLog() {
        // Default constructor for JPA
    }

    public ScoreAuditLog(UUID profileId, Score scoreChange, Score previousScore, Score newScore,
                        String reason, ScoreChangeType changeType, String externalReferenceId) {
        this.profileId = profileId;
        this.scoreChange = scoreChange;
        this.previousScore = previousScore;
        this.newScore = newScore;
        this.changeReason = new ScoreChangeReason(reason);
        this.changeType = changeType;
        this.externalReferenceId = externalReferenceId;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public Integer getScoreChange() {
        return scoreChange.value();
    }

    public Integer getPreviousScore() {
        return previousScore.value();
    }

    public Integer getNewScore() {
        return newScore.value();
    }

    public String getChangeReason() {
        return changeReason.reason();
    }

    public ScoreChangeType getChangeType() {
        return changeType;
    }

    public String getExternalReferenceId() {
        return externalReferenceId;
    }

    public enum ScoreChangeType {
        ADDITION,
        SUBTRACTION,
        CHALLENGE_COMPLETION,
        CHALLENGE_FAILURE,
        MANUAL_ADJUSTMENT,
        SYSTEM_CORRECTION
    }
}