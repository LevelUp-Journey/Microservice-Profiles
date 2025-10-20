package com.levelup.journey.platform.microserviceprofiles.competitive.infrastructure.persistence.jpa.repositories;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.aggregates.CompetitiveProfile;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.CompetitiveRank;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.CompetitiveUserId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Competitive Profile Repository
 * JPA repository for CompetitiveProfile aggregate persistence
 */
@Repository
public interface CompetitiveProfileRepository extends JpaRepository<CompetitiveProfile, UUID> {

    /**
     * Find competitive profile by user ID
     *
     * @param userId The user ID value object
     * @return Optional of CompetitiveProfile
     */
    Optional<CompetitiveProfile> findByUserId(CompetitiveUserId userId);

    /**
     * Check if competitive profile exists for user
     *
     * @param userId The user ID value object
     * @return true if profile exists
     */
    boolean existsByUserId(CompetitiveUserId userId);

    /**
     * Find all profiles by competitive rank
     *
     * @param rank The competitive rank enum
     * @return List of profiles with specified rank
     */
    List<CompetitiveProfile> findByCurrentRank(CompetitiveRank rank);

    /**
     * Find top N profiles ordered by total points descending
     *
     * @param pageable Pagination parameters
     * @return List of top profiles (leaderboard)
     */
    @Query("SELECT cp FROM CompetitiveProfile cp ORDER BY cp.totalPoints.value DESC, cp.createdAt ASC")
    List<CompetitiveProfile> findTopByOrderByTotalPointsDesc(Pageable pageable);

    /**
     * Get global leaderboard ordered by points
     *
     * @return List of all profiles ordered by total points descending
     */
    @Query("SELECT cp FROM CompetitiveProfile cp ORDER BY cp.totalPoints.value DESC, cp.createdAt ASC")
    List<CompetitiveProfile> findAllOrderedByTotalPoints();

    /**
     * Find profiles with points greater than or equal to minimum
     *
     * @param minPoints Minimum points threshold
     * @return List of qualifying profiles
     */
    @Query("SELECT cp FROM CompetitiveProfile cp WHERE cp.totalPoints.value >= :minPoints ORDER BY cp.totalPoints.value DESC")
    List<CompetitiveProfile> findByTotalPointsGreaterThanEqual(@Param("minPoints") Integer minPoints);

    /**
     * Count profiles with more points than given value
     * Used to calculate leaderboard position
     *
     * @param points Points threshold
     * @return Count of profiles with higher points
     */
    @Query("SELECT COUNT(cp) FROM CompetitiveProfile cp WHERE cp.totalPoints.value > :points")
    Long countProfilesWithMorePoints(@Param("points") Integer points);

    /**
     * Count profiles with same points but earlier creation date
     * Used for tie-breaking in leaderboard position
     *
     * @param points Points value
     * @param createdAt Creation timestamp
     * @return Count of profiles with same points but created earlier
     */
    @Query("SELECT COUNT(cp) FROM CompetitiveProfile cp WHERE cp.totalPoints.value = :points AND cp.createdAt < :createdAt")
    Long countProfilesWithSamePointsButEarlier(@Param("points") Integer points, @Param("createdAt") java.util.Date createdAt);

    /**
     * Find profiles in TOP500 range
     *
     * @param rank The TOP500 rank enum
     * @return List of TOP500 profiles
     */
    @Query("SELECT cp FROM CompetitiveProfile cp WHERE cp.currentRank = :rank ORDER BY cp.totalPoints.value DESC")
    List<CompetitiveProfile> findTop500Profiles(@Param("rank") CompetitiveRank rank);
}
