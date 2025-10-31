package com.levelup.journey.platform.microserviceprofiles.leaderboard.interfaces.acl;

import java.util.List;

/**
 * Leaderboard Context Facade
 * ACL interface for external bounded contexts to access Leaderboard BC data
 */
public interface LeaderboardContextFacade {

    /**
     * Get user's position in the leaderboard
     *
     * @param userId User identifier
     * @return Leaderboard position, or 0 if user not found
     */
    Integer getUserPosition(String userId);

    /**
     * Get top N users from leaderboard
     *
     * @param limit Number of top users to retrieve
     * @return List of user IDs in ranking order
     */
    List<String> getTopNUsers(Integer limit);

    /**
     * Get TOP 500 user IDs
     *
     * @return List of user IDs in TOP 500
     */
    List<String> getTop500UserIds();

    /**
     * Check if user is in TOP 500
     *
     * @param userId User identifier
     * @return true if user is in TOP 500
     */
    Boolean isUserInTop500(String userId);
}
