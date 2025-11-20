package com.levelup.journey.platform.microserviceprofiles.suggestions.infrastructure.persistence.jpa.repositories;

import com.levelup.journey.platform.microserviceprofiles.suggestions.domain.model.aggregates.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Suggestion Repository
 * JPA repository for Suggestion aggregate persistence
 */
@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
}
