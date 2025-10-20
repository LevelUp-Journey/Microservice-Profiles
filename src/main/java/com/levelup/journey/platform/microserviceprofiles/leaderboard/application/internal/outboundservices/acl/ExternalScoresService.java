package com.levelup.journey.platform.microserviceprofiles.leaderboard.application.internal.outboundservices.acl;

import com.levelup.journey.platform.microserviceprofiles.scores.interfaces.acl.ScoresContextFacade;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * External Scores Service
 * ACL implementation for accessing Scores BC from Leaderboard BC
 */
@Service
public class ExternalScoresService {

    private final ScoresContextFacade scoresContextFacade;

    public ExternalScoresService(ScoresContextFacade scoresContextFacade) {
        this.scoresContextFacade = scoresContextFacade;
    }

    /**
     * Fetch total points for a specific user
     *
     * @param userId User identifier
     * @return Optional of total points
     */
    public Optional<Integer> fetchTotalPointsByUserId(String userId) {
        var totalPoints = scoresContextFacade.getTotalPointsByUserId(userId);
        return totalPoints > 0 ? Optional.of(totalPoints) : Optional.empty();
    }

    /**
     * Fetch all users with their total points
     * Used for bulk leaderboard updates
     *
     * @return Map of userId to total points
     */
    public Map<String, Integer> fetchAllUserTotalPoints() {
        return scoresContextFacade.getAllUserTotalPoints();
    }

    /**
     * Check if a user has any scores
     *
     * @param userId User identifier
     * @return true if user has scores
     */
    public boolean userHasScores(String userId) {
        return scoresContextFacade.userHasScores(userId);
    }
}
