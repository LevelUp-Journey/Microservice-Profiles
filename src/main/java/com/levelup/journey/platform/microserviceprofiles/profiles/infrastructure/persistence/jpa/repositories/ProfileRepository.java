package com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.persistence.jpa.repositories;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.aggregates.Profile;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.Username;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Profile Repository
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    /**
     * Find a Profile by Username
     *
     * @param username The Username
     * @return A {@link Profile} instance if the username is valid, otherwise empty
     */
    Optional<Profile> findByUsername(Username username);

    /**
     * Check if a Profile exists by Username
     *
     * @param username The Username
     * @return True if the username exists, otherwise false
     */
    boolean existsByUsername(Username username);

    /**
     * Check if a Profile exists by Username string value
     *
     * @param username The username string value
     * @return True if the username exists, otherwise false
     */
    boolean existsByUsernameUsername(String username);

    /**
     * Find a Profile by User ID
     *
     * @param userId The User ID
     * @return A {@link Profile} instance if the user ID is valid, otherwise empty
     */
    Optional<Profile> findByUserId(UserId userId);

    /**
     * Check if a Profile exists by User ID
     *
     * @param userId The User ID
     * @return True if the user ID exists, otherwise false
     */
    boolean existsByUserId(UserId userId);

    /**
     * Search Profiles by username pattern (case-insensitive)
     *
     * @param usernamePattern The username pattern to search for
     * @return A list of {@link Profile} instances matching the pattern
     */
    java.util.List<Profile> findByUsernameUsernameContainingIgnoreCase(String usernamePattern);
}
