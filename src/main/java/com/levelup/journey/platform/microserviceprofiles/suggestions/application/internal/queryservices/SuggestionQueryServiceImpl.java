package com.levelup.journey.platform.microserviceprofiles.suggestions.application.internal.queryservices;

import com.levelup.journey.platform.microserviceprofiles.suggestions.domain.model.aggregates.Suggestion;
import com.levelup.journey.platform.microserviceprofiles.suggestions.domain.model.queries.GetAllSuggestionsQuery;
import com.levelup.journey.platform.microserviceprofiles.suggestions.domain.services.SuggestionQueryService;
import com.levelup.journey.platform.microserviceprofiles.suggestions.infrastructure.persistence.jpa.repositories.SuggestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Suggestion Query Service Implementation
 * Handles all query operations for suggestions
 */
@Service
public class SuggestionQueryServiceImpl implements SuggestionQueryService {

    private static final Logger logger = LoggerFactory.getLogger(SuggestionQueryServiceImpl.class);
    private final SuggestionRepository suggestionRepository;

    public SuggestionQueryServiceImpl(SuggestionRepository suggestionRepository) {
        this.suggestionRepository = suggestionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Suggestion> handle(GetAllSuggestionsQuery query) {
        logger.debug("Fetching all suggestions");
        return suggestionRepository.findAll();
    }
}
