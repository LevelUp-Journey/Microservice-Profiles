package com.levelup.journey.platform.microserviceprofiles.profiles.domain.services;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.aggregates.Profile;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.CreateProfileFromUserCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.UpdateProfileCommand;

import java.util.Optional;

/**
 * Profile Command Service
 */
public interface ProfileCommandService {
    /**
     * Handle Create Profile From User Command
     * Creates a profile from IAM user registration event
     *
     * @param command The {@link CreateProfileFromUserCommand} Command
     * @return A {@link Profile} instance if the command is valid, otherwise empty
     * @throws IllegalArgumentException if the user ID already exists
     */
    Optional<Profile> handle(CreateProfileFromUserCommand command);

    /**
     * Handle Update Profile Command
     * Updates an existing profile with new information
     *
     * @param command The {@link UpdateProfileCommand} Command
     * @return A {@link Profile} instance if the command is valid, otherwise empty
     * @throws IllegalArgumentException if the profile does not exist or username is already taken
     */
    Optional<Profile> handle(UpdateProfileCommand command);
}
