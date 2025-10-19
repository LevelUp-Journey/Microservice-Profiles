package com.levelup.journey.platform.microserviceprofiles.scores.domain.services;

import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.aggregates.Score;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.commands.RecordScoreFromChallengeCommand;

import java.util.Optional;

/**
 * Score Command Service
 * Handles commands related to score management
 */
public interface ScoreCommandService {
    
    /**
     * Handle Record Score From Challenge Command
     * Creates a new score record from a challenge completion event
     *
     * @param command The {@link RecordScoreFromChallengeCommand} Command
     * @return A {@link Score} instance if the command is valid, otherwise empty
     */
    Optional<Score> handle(RecordScoreFromChallengeCommand command);
}
