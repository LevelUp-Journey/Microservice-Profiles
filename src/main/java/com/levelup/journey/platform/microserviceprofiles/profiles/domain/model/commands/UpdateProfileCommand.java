package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.StudentCycle;

import java.util.UUID;

public record UpdateProfileCommand(
        UUID profileId,
        String firstName,
        String lastName,
        String username,
        String profileUrl,
        String provider,
        StudentCycle cycle
) {
}
