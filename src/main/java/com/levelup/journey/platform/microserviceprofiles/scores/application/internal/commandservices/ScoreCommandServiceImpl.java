package com.levelup.journey.platform.microserviceprofiles.scores.application.internal.commandservices;

import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.aggregates.Score;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.commands.RecordScoreFromChallengeCommand;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.services.ScoreCommandService;
import com.levelup.journey.platform.microserviceprofiles.scores.infrastructure.persistence.jpa.repositories.ScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Score Command Service Implementation
 * Handles command execution for score management
 */
@Service
public class ScoreCommandServiceImpl implements ScoreCommandService {
    
    private final ScoreRepository scoreRepository;

    public ScoreCommandServiceImpl(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    @Override
    @Transactional
    public Optional<Score> handle(RecordScoreFromChallengeCommand command) {
        // Create score from challenge completion
        var score = new Score(command);
        
        // Persist score
        var savedScore = scoreRepository.save(score);
        
        return Optional.of(savedScore);
    }
}
