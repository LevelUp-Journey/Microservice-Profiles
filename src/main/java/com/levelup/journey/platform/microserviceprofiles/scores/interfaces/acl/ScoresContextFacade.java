package com.levelup.journey.platform.microserviceprofiles.scores.interfaces.acl;

import java.util.List;
import java.util.Map;

/**
 * Scores Context Facade
 * ACL interface for external bounded contexts to access score data
 */
public interface ScoresContextFacade {

    /**
     * Get total points for a specific user
     *
     * @param userId The user identifier
     * @return Total points accumulated by the user, 0 if user has no scores
     */
    Integer getTotalPointsByUserId(String userId);

    /**
     * Get total points for all users
     * Used for leaderboard calculation
     *
     * @return Map of userId to total points
     */
    Map<String, Integer> getAllUserTotalPoints();

    /**
     * Check if a user has any scores
     *
     * @param userId The user identifier
     * @return true if user has at least one score record
     */
    boolean userHasScores(String userId);

    /**
     * Get users with points greater than or equal to threshold
     *
     * @param minPoints Minimum points threshold
     * @return List of user IDs meeting the criteria
     */
    List<String> getUsersWithMinimumPoints(Integer minPoints);
}
