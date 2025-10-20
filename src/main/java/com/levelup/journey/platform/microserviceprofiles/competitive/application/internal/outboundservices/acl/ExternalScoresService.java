package com.levelup.journey.platform.microserviceprofiles.competitive.application.internal.outboundservices.acl;

import com.levelup.journey.platform.microserviceprofiles.scores.interfaces.acl.ScoresContextFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * External Scores Service
 * ACL implementation for accessing score data from Scores bounded context
 */
@Service("competitiveExternalScoresService")
public class ExternalScoresService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalScoresService.class);
    private final ScoresContextFacade scoresContextFacade;

    public ExternalScoresService(ScoresContextFacade scoresContextFacade) {
        this.scoresContextFacade = scoresContextFacade;
    }

    /**
     * Fetch total points for a specific user from Scores BC
     *
     * @param userId User identifier
     * @return Optional of total points, empty if user has no scores
     */
    public Optional<Integer> fetchTotalPointsByUserId(String userId) {
        try {
            Integer totalPoints = scoresContextFacade.getTotalPointsByUserId(userId);

            if (totalPoints == null || totalPoints == 0) {
                logger.debug("No scores found for user: {}", userId);
                return Optional.empty();
            }

            logger.debug("Fetched total points for user {}: {}", userId, totalPoints);
            return Optional.of(totalPoints);

        } catch (Exception e) {
            logger.error("Error fetching total points for user {}: {}", userId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Fetch total points for all users from Scores BC
     * Used for leaderboard calculations
     *
     * @return Map of userId to total points
     */
    public Map<String, Integer> fetchAllUserTotalPoints() {
        try {
            Map<String, Integer> userPointsMap = scoresContextFacade.getAllUserTotalPoints();
            logger.debug("Fetched total points for {} users", userPointsMap.size());
            return userPointsMap;

        } catch (Exception e) {
            logger.error("Error fetching all user total points: {}", e.getMessage());
            return Map.of();
        }
    }

    /**
     * Check if a user has any scores in Scores BC
     *
     * @param userId User identifier
     * @return true if user has at least one score
     */
    public boolean userHasScores(String userId) {
        try {
            return scoresContextFacade.userHasScores(userId);
        } catch (Exception e) {
            logger.error("Error checking if user {} has scores: {}", userId, e.getMessage());
            return false;
        }
    }

    /**
     * Fetch users with minimum points threshold
     *
     * @param minPoints Minimum points required
     * @return Map of qualifying userId to total points
     */
    public Map<String, Integer> fetchUsersWithMinimumPoints(Integer minPoints) {
        try {
            var qualifyingUserIds = scoresContextFacade.getUsersWithMinimumPoints(minPoints);
            var allUserPoints = scoresContextFacade.getAllUserTotalPoints();

            return qualifyingUserIds.stream()
                    .filter(allUserPoints::containsKey)
                    .collect(java.util.stream.Collectors.toMap(
                            userId -> userId,
                            allUserPoints::get
                    ));

        } catch (Exception e) {
            logger.error("Error fetching users with minimum points {}: {}", minPoints, e.getMessage());
            return Map.of();
        }
    }

    /**
     * Validate that user exists in Scores BC before creating competitive profile
     *
     * @param userId User identifier
     * @return true if user can have a competitive profile
     */
    public boolean validateUserEligibility(String userId) {
        return userHasScores(userId);
    }
}
