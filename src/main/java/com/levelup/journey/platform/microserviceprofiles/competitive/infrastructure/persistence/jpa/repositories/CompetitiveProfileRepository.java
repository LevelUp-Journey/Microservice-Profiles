package com.levelup.journey.platform.microserviceprofiles.competitive.infrastructure.persistence.jpa.repositories;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.aggregates.CompetitiveProfile;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.entities.Rank;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.CompetitiveUserId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
     * Find all profiles by competitive rank entity
     *
     * @param rank The rank entity
     * @return List of profiles with specified rank
     */
    List<CompetitiveProfile> findByCurrentRank(Rank rank);

    /**
     * Find paginated profiles by competitive rank entity
     *
     * @param rank The rank entity
     * @param pageable Pagination parameters
     * @return List of profiles with specified rank (paginated)
     */
    List<CompetitiveProfile> findByCurrentRank(Rank rank, Pageable pageable);

    /**
     * Count profiles by competitive rank entity
     *
     * @param rank The rank entity
     * @return Count of profiles with specified rank
     */
    Long countByCurrentRank(Rank rank);
}
