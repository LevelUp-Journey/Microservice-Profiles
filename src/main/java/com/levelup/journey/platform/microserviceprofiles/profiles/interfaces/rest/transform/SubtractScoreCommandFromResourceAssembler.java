package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.SubtractScoreCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources.SubtractScoreResource;

import java.util.UUID;

public class SubtractScoreCommandFromResourceAssembler {

    public static SubtractScoreCommand toCommandFromResource(UUID profileId, SubtractScoreResource resource) {
        return new SubtractScoreCommand(
            profileId,
            resource.points(),
            resource.reason(),
            resource.externalReferenceId()
        );
    }
}