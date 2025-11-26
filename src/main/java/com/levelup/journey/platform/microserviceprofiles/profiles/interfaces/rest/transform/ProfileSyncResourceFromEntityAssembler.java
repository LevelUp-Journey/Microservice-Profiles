package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.aggregates.Profile;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources.ProfileSyncResource;

/**
 * Assembler to convert a Profile entity to a ProfileSyncResource.
 * Used for synchronization operations to transform profiles into
 * a lightweight format suitable for Kafka messaging.
 */
public class ProfileSyncResourceFromEntityAssembler {
    /**
     * Converts a Profile entity to a ProfileSyncResource.
     *
     * @param entity The {@link Profile} entity to convert.
     * @return The {@link ProfileSyncResource} resource.
     */
    public static ProfileSyncResource toResourceFromEntity(Profile entity) {
        return new ProfileSyncResource(
                entity.getUserId(),
                entity.getId().toString(),
                entity.getUsername(),
                entity.getProfileUrl()
        );
    }
}
