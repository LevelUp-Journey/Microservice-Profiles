package com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.persistence.jpa.repositories;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.entities.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RankRepository extends JpaRepository<Rank, UUID> {

    Optional<Rank> findByRankName_Name(String name);

    @Query("SELECT r FROM Rank r WHERE :score BETWEEN r.minScore AND r.maxScore")
    Optional<Rank> findRankByScore(@Param("score") Integer score);

    List<Rank> findAllByOrderByRankOrderAsc();

    boolean existsByRankName_Name(String name);
}