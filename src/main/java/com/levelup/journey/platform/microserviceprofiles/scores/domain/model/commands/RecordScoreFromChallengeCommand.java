package com.levelup.journey.platform.microserviceprofiles.scores.domain.model.commands;

/**
 * Command to record score points from a challenge completion
 */
public record RecordScoreFromChallengeCommand(
        String userId,
        String challengeId,
        String challengeType,
        Integer points,
        Long executionTimeMs
) {
    public RecordScoreFromChallengeCommand {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (challengeId == null || challengeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Challenge ID cannot be null or empty");
        }
        if (challengeType == null || challengeType.trim().isEmpty()) {
            throw new IllegalArgumentException("Challenge type cannot be null or empty");
        }
        if (points == null || points <= 0) {
            throw new IllegalArgumentException("Points must be greater than zero");
        }
        if (executionTimeMs == null || executionTimeMs < 0) {
            throw new IllegalArgumentException("Execution time cannot be negative");
        }
    }
}
