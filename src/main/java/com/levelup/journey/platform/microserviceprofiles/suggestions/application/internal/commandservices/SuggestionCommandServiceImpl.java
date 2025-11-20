package com.levelup.journey.platform.microserviceprofiles.suggestions.application.internal.commandservices;

import com.levelup.journey.platform.microserviceprofiles.suggestions.domain.model.aggregates.Suggestion;
import com.levelup.journey.platform.microserviceprofiles.suggestions.domain.model.commands.CreateSuggestionCommand;
import com.levelup.journey.platform.microserviceprofiles.suggestions.domain.services.SuggestionCommandService;
import com.levelup.journey.platform.microserviceprofiles.suggestions.infrastructure.persistence.jpa.repositories.SuggestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Suggestion Command Service Implementation
 * Handles all command operations for suggestions
 */
@Service
public class SuggestionCommandServiceImpl implements SuggestionCommandService {

    private static final Logger logger = LoggerFactory.getLogger(SuggestionCommandServiceImpl.class);
    private final SuggestionRepository suggestionRepository;

    public SuggestionCommandServiceImpl(SuggestionRepository suggestionRepository) {
        this.suggestionRepository = suggestionRepository;
    }

    @Override
    @Transactional
    public Optional<Suggestion> handle(CreateSuggestionCommand command) {
        logger.info("Creating new suggestion");

        var suggestion = new Suggestion(command);
        var savedSuggestion = suggestionRepository.save(suggestion);

        logger.info("Suggestion created successfully with id: {}", savedSuggestion.getId());
        return Optional.of(savedSuggestion);
    }
}
