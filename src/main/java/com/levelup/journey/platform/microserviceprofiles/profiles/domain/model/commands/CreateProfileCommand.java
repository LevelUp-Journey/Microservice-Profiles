package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands;

public record CreateProfileCommand(
        String firstName,
        String lastName,
        String profileUrl
) {
}
