package com.levelup.journey.platform.microserviceprofiles.scores.domain.model.valueobjects;

/**
 * Enum representing the source/reason for score points
 */
public enum ScoreSource {
    CHALLENGE_COMPLETED("Challenge Completed"),
    ACHIEVEMENT_UNLOCKED("Achievement Unlocked"),
    DAILY_LOGIN("Daily Login Bonus"),
    REFERRAL("Referral Bonus"),
    MANUAL_ADJUSTMENT("Manual Adjustment");

    private final String displayName;

    ScoreSource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
