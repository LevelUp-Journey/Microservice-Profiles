package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.AddScoreCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources.AddScoreResource;

import java.util.UUID;

public class AddScoreCommandFromResourceAssembler {

    public static AddScoreCommand toCommandFromResource(UUID profileId, AddScoreResource resource) {
        return new AddScoreCommand(
            profileId,
            resource.points(),
            resource.reason(),
            resource.externalReferenceId()
        );
    }
}