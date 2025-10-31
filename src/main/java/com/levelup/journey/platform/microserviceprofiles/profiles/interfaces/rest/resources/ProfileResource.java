package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.StudentCycle;

import java.util.UUID;

/**
 * Resource for a profile.
 */
public record ProfileResource(
        UUID id,
        String username,
        String profileUrl,
        String provider,
        String firstName,
        String lastName,
        StudentCycle cycle
) {
}