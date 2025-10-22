package com.levelup.journey.platform.microserviceprofiles.leaderboard.infrastructure.persistence.jpa.repositories;

import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.aggregates.LeaderboardEntry;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.valueobjects.LeaderboardUserId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Count entries with points higher than given value
     * Used for efficient position calculation
     *
     * @param points Points threshold
     * @return Count of entries with higher points
     */
    @Query("SELECT COUNT(le) FROM LeaderboardEntry le WHERE le.totalPoints.points > :points")
    Long countEntriesWithHigherPoints(@Param("points") Integer points);

    /**
     * Calculate position for a user considering tie-breaking rules
     * Position = entries with higher points + entries with same points but created earlier + 1
     *
     * This ensures unique positions even when users have the same points.
     * Tie-breaking rule: users created first get better (lower) positions.
     *
     * Formula:
     * - Count entries with MORE points than current user
     * - Count entries with SAME points but EARLIER created_at
     * - Add 1 to get 1-based position
     *
     * @param userId The user ID to calculate position for
     * @return The calculated position (1-based)
     */
    @Query("""
        SELECT
        (SELECT COUNT(le2) FROM LeaderboardEntry le2
         WHERE le2.totalPoints.points > le.totalPoints.points)
        +
        (SELECT COUNT(le3) FROM LeaderboardEntry le3
         WHERE le3.totalPoints.points = le.totalPoints.points
         AND le3.createdAt < le.createdAt)
        + 1
        FROM LeaderboardEntry le
        WHERE le.userId = :userId
        """)
    Long calculatePositionForUser(@Param("userId") LeaderboardUserId userId);

    /**
     * Count total number of leaderboard entries
     *
     * @return Total count of entries
     */
    @Query("SELECT COUNT(le) FROM LeaderboardEntry le")
    Long countTotalEntries();
}
