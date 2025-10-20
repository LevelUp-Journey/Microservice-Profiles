package com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.CreateCompetitiveProfileCommand;
import com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.resources.CreateCompetitiveProfileResource;

/**
 * Create Competitive Profile Command From Resource Assembler
 * Transforms CreateCompetitiveProfileResource to CreateCompetitiveProfileCommand
 */
public class CreateCompetitiveProfileCommandFromResourceAssembler {

    /**
     * Transform CreateCompetitiveProfileResource to CreateCompetitiveProfileCommand
     *
     * @param resource The CreateCompetitiveProfileResource
     * @return CreateCompetitiveProfileCommand
     */
    public static CreateCompetitiveProfileCommand toCommandFromResource(CreateCompetitiveProfileResource resource) {
        return new CreateCompetitiveProfileCommand(resource.userId());
    }
}
