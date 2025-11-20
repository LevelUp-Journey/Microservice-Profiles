package com.levelup.journey.platform.microserviceprofiles.suggestions.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.suggestions.domain.model.commands.CreateSuggestionCommand;
import com.levelup.journey.platform.microserviceprofiles.suggestions.interfaces.rest.resources.CreateSuggestionResource;

/**
 * Assembler to convert CreateSuggestionResource to CreateSuggestionCommand
 */
public class CreateSuggestionCommandFromResourceAssembler {

    public static CreateSuggestionCommand toCommandFromResource(CreateSuggestionResource resource) {
        return new CreateSuggestionCommand(resource.comment());
    }
}
