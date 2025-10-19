package com.levelup.journey.platform.microserviceprofiles.scores.application.internal.queryservices;

import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.aggregates.Score;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.queries.GetAllScoresQuery;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.queries.GetScoresByUserIdQuery;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.queries.GetTotalPointsByUserIdQuery;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.services.ScoreQueryService;
import com.levelup.journey.platform.microserviceprofiles.scores.infrastructure.persistence.jpa.repositories.ScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Score Query Service Implementation
 * Handles query execution for score retrieval
 */
@Service
public class ScoreQueryServiceImpl implements ScoreQueryService {
    
    private final ScoreRepository scoreRepository;

    public ScoreQueryServiceImpl(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Score> handle(GetScoresByUserIdQuery query) {
        return scoreRepository.findByUserId(query.userId());
    }

    @Override
    @Transactional(readOnly = true)
    public Integer handle(GetTotalPointsByUserIdQuery query) {
        return scoreRepository.sumPointsByUserId(query.userId().userId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Score> handle(GetAllScoresQuery query) {
        return scoreRepository.findAll();
    }
}
