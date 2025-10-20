package com.levelup.journey.platform.microserviceprofiles.leaderboard.infrastructure.persistence.jpa.repositories;

import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.aggregates.LeaderboardEntry;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.valueobjects.LeaderboardUserId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Leaderboard Entry Repository
 * JPA repository for LeaderboardEntry aggregate persistence
 */
@Repository
public interface LeaderboardEntryRepository extends JpaRepository<LeaderboardEntry, UUID> {

    /**
     * Find leaderboard entry by user ID
     *
     * @param userId The user ID value object
     * @return Optional of LeaderboardEntry
     */
    Optional<LeaderboardEntry> findByUserId(LeaderboardUserId userId);

    /**
     * Check if leaderboard entry exists for user
     *
     * @param userId The user ID value object
     * @return true if entry exists
     */
    boolean existsByUserId(LeaderboardUserId userId);

    /**
     * Find all entries ordered by total points descending (for ranking)
     *
     * @return List of all entries ordered by points
     */
    @Query("SELECT le FROM LeaderboardEntry le ORDER BY le.totalPoints.points DESC, le.createdAt ASC")
    List<LeaderboardEntry> findAllOrderedByPointsDesc();

    /**
     * Find top N entries ordered by total points descending with pagination
     *
     * @param pageable Pagination parameters
     * @return List of top entries
     */
    @Query("SELECT le FROM LeaderboardEntry le ORDER BY le.totalPoints.points DESC, le.createdAt ASC")
    List<LeaderboardEntry> findTopEntriesByPoints(Pageable pageable);

    /**
     * Find top 500 entries
     *
     * @param pageable Pagination with limit 500
     * @return List of top 500 entries
     */
    @Query("SELECT le FROM LeaderboardEntry le ORDER BY le.totalPoints.points DESC, le.createdAt ASC")
    List<LeaderboardEntry> findTop500(Pageable pageable);
}
