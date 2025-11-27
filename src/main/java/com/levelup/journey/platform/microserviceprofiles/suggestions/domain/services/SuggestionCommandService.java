package com.levelup.journey.platform.microserviceprofiles.suggestions.domain.services;

import com.levelup.journey.platform.microserviceprofiles.suggestions.domain.model.aggregates.Suggestion;
import com.levelup.journey.platform.microserviceprofiles.suggestions.domain.model.commands.CreateSuggestionCommand;

import java.util.Optional;

/**
 * Suggestion Command Service
 * Handles commands related to suggestion operations
 */
public interface SuggestionCommandService {

    /**
     * Handle Create Suggestion Command
     * Creates a new suggestion
     *
     * @param command The {@link CreateSuggestionCommand}
     * @return Optional of created {@link Suggestion}
     */
    Optional<Suggestion> handle(CreateSuggestionCommand command);
}
