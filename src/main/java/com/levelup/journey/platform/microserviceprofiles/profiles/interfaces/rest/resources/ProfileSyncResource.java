package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources;

/**
 * Resource for profile synchronization.
 * Contains essential profile information to be sent to Kafka for consumption by other microservices.
 */
public record ProfileSyncResource(
        String userId,
        String profileId,
        String username,
        String profileUrl
) {
}
