package com.levelup.journey.platform.microserviceprofiles.profiles.application.internal.commandservices;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.aggregates.Profile;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.CreateProfileCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.CreateProfileRankCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileCommandService;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileRankCommandService;
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
    private final ProfileRankCommandService profileRankCommandService;

    /**
     * Constructor
     *
     * @param profileRepository The {@link ProfileRepository} instance
     * @param usernameGeneratorService The {@link UsernameGeneratorService} instance
     * @param profileRankCommandService The {@link ProfileRankCommandService} instance
     */
    public ProfileCommandServiceImpl(ProfileRepository profileRepository,
                                   UsernameGeneratorService usernameGeneratorService,
                                   ProfileRankCommandService profileRankCommandService) {
        this.profileRepository = profileRepository;
        this.usernameGeneratorService = usernameGeneratorService;
        this.profileRankCommandService = profileRankCommandService;
    }

    // inherited javadoc
    @Override
    @Transactional
    public Optional<Profile> handle(CreateProfileCommand command) {
        var username = usernameGeneratorService.generateUniqueUsername();
        var profile = new Profile(command, username);
        var savedProfile = profileRepository.save(profile);

        // Automatically create profile rank for the new profile
        var createProfileRankCommand = new CreateProfileRankCommand(savedProfile.getId());
        profileRankCommandService.handle(createProfileRankCommand);

        return Optional.of(savedProfile);
    }
}
