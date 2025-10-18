package com.levelup.journey.platform.microserviceprofiles.profiles.application.internal.commandservices;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.aggregates.Profile;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.CreateProfileCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.CreateProfileFromUserCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileCommandService;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.UsernameGeneratorService;
import com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Profile Command Service Implementation
 */
@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {
    private final ProfileRepository profileRepository;
    private final UsernameGeneratorService usernameGeneratorService;

    /**
     * Constructor
     *
     * @param profileRepository The {@link ProfileRepository} instance
     * @param usernameGeneratorService The {@link UsernameGeneratorService} instance
     */
    public ProfileCommandServiceImpl(ProfileRepository profileRepository,
                                   UsernameGeneratorService usernameGeneratorService) {
        this.profileRepository = profileRepository;
        this.usernameGeneratorService = usernameGeneratorService;
    }

    // inherited javadoc
    @Override
    @Transactional
    public Optional<Profile> handle(CreateProfileCommand command) {
        var username = usernameGeneratorService.generateUniqueUsername();
        var profile = new Profile(command, username);
        var savedProfile = profileRepository.save(profile);

        return Optional.of(savedProfile);
    }

    // inherited javadoc
    @Override
    @Transactional
    public Optional<Profile> handle(CreateProfileFromUserCommand command) {
        var userId = new UserId(command.userId());

        // Check if profile already exists for this user ID
        if (profileRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("Profile already exists for user ID: " + command.userId());
        }

        // Generate unique username
        var username = usernameGeneratorService.generateUniqueUsername();

        // Create and save profile
        var profile = new Profile(command, username);
        var savedProfile = profileRepository.save(profile);

        return Optional.of(savedProfile);
    }
}
