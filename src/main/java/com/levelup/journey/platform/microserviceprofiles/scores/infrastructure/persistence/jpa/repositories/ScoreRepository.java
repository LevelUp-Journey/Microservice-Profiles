package com.levelup.journey.platform.microserviceprofiles.scores.infrastructure.persistence.jpa.repositories;

import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.aggregates.Score;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.valueobjects.ScoreUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Score Repository
 * JPA repository for Score aggregate persistence
 */
@Repository
public interface ScoreRepository extends JpaRepository<Score, UUID> {
    
    /**
     * Find all scores by user ID
     * @param userId The user ID
     * @return List of scores for the user
     */
    List<Score> findByUserId(ScoreUserId userId);

    /**
     * Calculate total points for a user
     * @param userId The user ID string
     * @return Total points sum
     */
    @Query("SELECT COALESCE(SUM(s.points.value), 0) FROM Score s WHERE s.userId.userId = :userId")
    Integer sumPointsByUserId(@Param("userId") String userId);

    /**
     * Check if user has any scores
     * @param userId The user ID
     * @return true if user has scores
     */
    boolean existsByUserId(ScoreUserId userId);
}
