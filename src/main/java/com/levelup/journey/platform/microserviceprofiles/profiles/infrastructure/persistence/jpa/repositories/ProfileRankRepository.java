package com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.persistence.jpa.repositories;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.aggregates.ProfileRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRankRepository extends JpaRepository<ProfileRank, UUID> {

    Optional<ProfileRank> findByProfileId(UUID profileId);

    @Query("SELECT pr FROM ProfileRank pr ORDER BY pr.currentScore DESC")
    List<ProfileRank> findTopProfilesOrderByCurrentScoreDesc(@Param("limit") int limit);

    @Query("SELECT COUNT(pr) FROM ProfileRank pr WHERE pr.currentScore > :score")
    Long countProfilesWithHigherScore(@Param("score") Integer score);

    boolean existsByProfileId(UUID profileId);

    @Query("SELECT pr FROM ProfileRank pr WHERE pr.currentScore >= :minScore ORDER BY pr.currentScore DESC")
    List<ProfileRank> findProfilesWithScoreGreaterThanEqual(@Param("minScore") Integer minScore);
}