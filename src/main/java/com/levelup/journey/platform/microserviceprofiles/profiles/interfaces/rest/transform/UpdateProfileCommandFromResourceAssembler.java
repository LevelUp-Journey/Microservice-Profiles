package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.UpdateProfileCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources.UpdateProfileResource;

import java.util.UUID;

public class UpdateProfileCommandFromResourceAssembler {
    public static UpdateProfileCommand toCommandFromResource(UUID profileId, UpdateProfileResource resource) {
        return new UpdateProfileCommand(
                profileId,
                resource.firstName(),
                resource.lastName(),
                resource.username(),
                resource.profileUrl(),
                resource.provider()
        );
    }
}
