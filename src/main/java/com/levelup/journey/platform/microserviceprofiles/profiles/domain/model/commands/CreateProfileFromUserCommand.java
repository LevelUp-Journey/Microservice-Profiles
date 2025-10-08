package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands;

/**
 * Command to create a profile from IAM user registration event
 */
public record CreateProfileFromUserCommand(
        String userId,
        String firstName,
        String lastName,
        String profileUrl
) {
}
