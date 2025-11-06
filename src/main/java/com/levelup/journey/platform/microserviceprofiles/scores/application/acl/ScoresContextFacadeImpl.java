package com.levelup.journey.platform.microserviceprofiles.scores.application.acl;

import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.valueobjects.ScoreUserId;
import com.levelup.journey.platform.microserviceprofiles.scores.infrastructure.persistence.jpa.repositories.ScoreRepository;
import com.levelup.journey.platform.microserviceprofiles.scores.interfaces.acl.ScoresContextFacade;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scores Context Facade Implementation
 * ACL implementation providing score data access to external bounded contexts
 */
@Service
public class ScoresContextFacadeImpl implements ScoresContextFacade {

    private final ScoreRepository scoreRepository;

    public ScoresContextFacadeImpl(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    @Override
    public Integer getTotalPointsByUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            return 0;
        }
        return scoreRepository.sumPointsByUserId(userId);
    }

    @Override
    public Map<String, Integer> getAllUserTotalPoints() {
        List<Object[]> results = scoreRepository.findAllUserTotalPoints();
        Map<String, Integer> userPointsMap = new HashMap<>();

        for (Object[] result : results) {
            String userId = (String) result[0];
            Long totalPoints = (Long) result[1];
            userPointsMap.put(userId, totalPoints.intValue());
        }

        return userPointsMap;
    }

    @Override
    public boolean userHasScores(String userId) {
        if (userId == null || userId.isBlank()) {
            return false;
        }
        return scoreRepository.existsByUserId(new ScoreUserId(userId));
    }

    @Override
    public List<String> getUsersWithMinimumPoints(Integer minPoints) {
        if (minPoints == null || minPoints < 0) {
            minPoints = 0;
        }
        return scoreRepository.findUserIdsWithMinimumPoints(minPoints);
    }
}
