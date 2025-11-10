package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.acl;

/**
 * Profiles Context Facade
 * ACL interface for accessing Profile information from other bounded contexts
 * (Competitive, Leaderboard, etc.)
 *
 * This facade provides a simple interface for other bounded contexts to access
 * profile data without depending on internal domain model details.
 */
public interface ProfilesContextFacade {

    /**
     * Get username by user ID
     *
     * @param userId The user's unique identifier
     * @return The username, or null if not found
     */
    String getUsernameByUserId(String userId);

    /**
     * Get profile ID by user ID
     *
     * @param userId The user's unique identifier
     * @return The profile ID, or null if not found
     */
    String getProfileIdByUserId(String userId);
}
