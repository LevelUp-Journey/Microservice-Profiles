package com.levelup.journey.platform.microserviceprofiles.suggestions.domain.services;

import com.levelup.journey.platform.microserviceprofiles.suggestions.domain.model.aggregates.Suggestion;
import com.levelup.journey.platform.microserviceprofiles.suggestions.domain.model.queries.GetAllSuggestionsQuery;

import java.util.List;

/**
 * Suggestion Query Service
 * Handles queries related to suggestions
 */
public interface SuggestionQueryService {

    /**
     * Handle Get All Suggestions Query
     * Retrieves all suggestions
     *
     * @param query The {@link GetAllSuggestionsQuery}
     * @return List of {@link Suggestion}
     */
    List<Suggestion> handle(GetAllSuggestionsQuery query);
}
