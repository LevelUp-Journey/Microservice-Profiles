package com.levelup.journey.platform.microserviceprofiles.suggestions.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.suggestions.domain.model.aggregates.Suggestion;
import com.levelup.journey.platform.microserviceprofiles.suggestions.interfaces.rest.resources.SuggestionResource;

/**
 * Assembler to convert Suggestion entity to SuggestionResource
 */
public class SuggestionResourceFromEntityAssembler {

    public static SuggestionResource toResourceFromEntity(Suggestion entity) {
        return new SuggestionResource(
                entity.getId(),
                entity.getComment(),
                entity.getCreatedAt(),
                entity.getIsResolved()
        );
    }
}
