package com.levelup.journey.platform.microserviceprofiles.scores.application.internal.commandservices;

import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.aggregates.Score;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.commands.RecordScoreFromChallengeCommand;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.events.ScoreUpdatedEvent;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.services.ScoreCommandService;
import com.levelup.journey.platform.microserviceprofiles.scores.infrastructure.persistence.jpa.repositories.ScoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Score Command Service Implementation
 * Handles command execution for score management
 */
@Service
public class ScoreCommandServiceImpl implements ScoreCommandService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScoreCommandServiceImpl.class);
    private final ScoreRepository scoreRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ScoreCommandServiceImpl(ScoreRepository scoreRepository, 
                                   ApplicationEventPublisher eventPublisher) {
        this.scoreRepository = scoreRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Optional<Score> handle(RecordScoreFromChallengeCommand command) {
        // Create score from challenge completion
        var score = new Score(command);
        
        // Persist score
        var savedScore = scoreRepository.save(score);
        
        // Calculate new total points for the user
        var totalPoints = scoreRepository.sumPointsByUserId(savedScore.getUserId());
        
        // Publish domain event for other contexts
        var event = new ScoreUpdatedEvent(
            savedScore.getUserId(),
            totalPoints,
            savedScore.getPoints(),
            savedScore.getSource().name()
        );
        eventPublisher.publishEvent(event);
        
        logger.debug("Published ScoreUpdatedEvent for user: {} with total points: {}", 
            savedScore.getUserId(), totalPoints);
        
        return Optional.of(savedScore);
    }
}

