package com.levelup.journey.platform.microserviceprofiles.competitive.application.internal.outboundservices.acl;

import com.levelup.journey.platform.microserviceprofiles.leaderboard.interfaces.acl.LeaderboardContextFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * External Leaderboard Service
 * ACL implementation for accessing leaderboard data from Leaderboard bounded context
 */
@Service
public class ExternalLeaderboardService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalLeaderboardService.class);
    private final LeaderboardContextFacade leaderboardContextFacade;

    public ExternalLeaderboardService(LeaderboardContextFacade leaderboardContextFacade) {
        this.leaderboardContextFacade = leaderboardContextFacade;
    }

    /**
     * Fetch total time to achieve points for a specific user from Leaderboard BC
     *
     * @param userId User identifier
     * @return Optional of total time in milliseconds, empty if user not found
     */
    public Optional<Long> fetchTotalTimeToAchievePointsByUserId(String userId) {
        try {
            Long totalTimeMs = leaderboardContextFacade.getTotalTimeToAchievePointsByUserId(userId);

            if (totalTimeMs == null || totalTimeMs == 0) {
                logger.debug("No time data found for user: {}", userId);
                return Optional.empty();
            }

            logger.debug("Fetched total time for user {}: {} ms", userId, totalTimeMs);
            return Optional.of(totalTimeMs);

        } catch (Exception e) {
            logger.error("Error fetching total time for user {}: {}", userId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Check if user is in TOP 500 leaderboard
     *
     * @param userId User identifier
     * @return true if user is in TOP 500
     */
    public boolean isUserInTop500(String userId) {
        try {
            return leaderboardContextFacade.isUserInTop500(userId);
        } catch (Exception e) {
            logger.error("Error checking if user {} is in TOP 500: {}", userId, e.getMessage());
            return false;
        }
    }

    /**
     * Get user's leaderboard position
     *
     * @param userId User identifier
     * @return Position or 0 if not found
     */
    public Integer getUserPosition(String userId) {
        try {
            return leaderboardContextFacade.getUserPosition(userId);
        } catch (Exception e) {
            logger.error("Error getting position for user {}: {}", userId, e.getMessage());
            return 0;
        }
    }
}
