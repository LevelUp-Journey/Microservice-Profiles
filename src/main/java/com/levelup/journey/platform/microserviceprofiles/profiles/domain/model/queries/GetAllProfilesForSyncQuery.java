package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries;

/**
 * Query to get all profiles for synchronization with external services.
 * This query is used by the sync endpoint to retrieve all profiles
 * and publish them to Kafka for consumption by other microservices.
 */
public record GetAllProfilesForSyncQuery() {
}
