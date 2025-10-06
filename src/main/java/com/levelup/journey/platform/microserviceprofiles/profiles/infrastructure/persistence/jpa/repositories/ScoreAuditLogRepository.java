package com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.persistence.jpa.repositories;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.entities.ScoreAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScoreAuditLogRepository extends JpaRepository<ScoreAuditLog, UUID> {

    List<ScoreAuditLog> findByProfileIdOrderByCreatedAtDesc(UUID profileId);

    @Query("SELECT sal FROM ScoreAuditLog sal WHERE sal.profileId = :profileId ORDER BY sal.createdAt DESC")
    List<ScoreAuditLog> findScoreHistoryByProfileId(@Param("profileId") UUID profileId);

    @Query("SELECT sal FROM ScoreAuditLog sal WHERE sal.externalReferenceId = :externalReferenceId")
    List<ScoreAuditLog> findByExternalReferenceId(@Param("externalReferenceId") String externalReferenceId);

    @Query("SELECT COUNT(sal) FROM ScoreAuditLog sal WHERE sal.profileId = :profileId")
    Long countScoreChangesByProfileId(@Param("profileId") UUID profileId);
}