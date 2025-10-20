package com.levelup.journey.platform.microserviceprofiles.competitive.infrastructure.persistence.jpa.repositories;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.entities.Rank;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.CompetitiveRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Rank Repository
 * JPA repository for Rank entity (master/reference data)
 */
@Repository
public interface RankRepository extends JpaRepository<Rank, UUID> {

    /**
     * Find rank by name
     *
     * @param rankName The competitive rank enum value
     * @return Optional of Rank entity
     */
    Optional<Rank> findByRankName(CompetitiveRank rankName);

    /**
     * Find appropriate rank for given points
     * Returns the highest rank that the points qualify for
     *
     * @param points Total points
     * @return Optional of Rank entity
     */
    @Query("SELECT r FROM Rank r WHERE r.minimumPoints <= :points AND r.rankName != 'TOP500' ORDER BY r.minimumPoints DESC")
    Optional<Rank> findRankByPoints(@Param("points") Integer points);
}
