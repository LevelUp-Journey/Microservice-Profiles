package com.levelup.journey.platform.microserviceprofiles.profiles.application.internal.commandservices;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.aggregates.Profile;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.CreateProfileFromUserCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.UpdateProfileCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.events.ProfileCreatedEvent;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.events.ProfileRegisteredEvent;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.Username;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileCommandService;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.UsernameGeneratorService;
import com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.messaging.kafka.ProfileRegisteredKafkaPublisher;
import com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Profile Command Service Implementation
 */
@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {
    private static final Logger logger = LoggerFactory.getLogger(ProfileCommandServiceImpl.class);

    private final ProfileRepository profileRepository;
    private final UsernameGeneratorService usernameGeneratorService;
    private final ApplicationEventPublisher eventPublisher;
    private final ProfileRegisteredKafkaPublisher profileRegisteredKafkaPublisher;

    /**
     * Constructor
     *
     * @param profileRepository The {@link ProfileRepository} instance
     * @param usernameGeneratorService The {@link UsernameGeneratorService} instance
     * @param eventPublisher The {@link ApplicationEventPublisher} instance for domain events
     * @param profileRegisteredKafkaPublisher The {@link ProfileRegisteredKafkaPublisher} instance for Kafka events
     */
    public ProfileCommandServiceImpl(ProfileRepository profileRepository,
                                   UsernameGeneratorService usernameGeneratorService,
                                   ApplicationEventPublisher eventPublisher,
                                   ProfileRegisteredKafkaPublisher profileRegisteredKafkaPublisher) {
        this.profileRepository = profileRepository;
        this.usernameGeneratorService = usernameGeneratorService;
        this.eventPublisher = eventPublisher;
        this.profileRegisteredKafkaPublisher = profileRegisteredKafkaPublisher;
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

        // Publish internal domain event for other bounded contexts to react (Spring ApplicationEventPublisher)
        var profileCreatedEvent = new ProfileCreatedEvent(
                savedProfile.getUserId(),
                savedProfile.getFirstName(),
                savedProfile.getLastName(),
                savedProfile.getUsername(),
                savedProfile.getProfileUrl(),
                savedProfile.getProvider()
        );
        eventPublisher.publishEvent(profileCreatedEvent);

        logger.info("Profile created for user ID: {} with username: {}. ProfileCreatedEvent published.",
                savedProfile.getUserId(), savedProfile.getUsername());

        // Publish Kafka event to community-registration topic for external microservices
        var profileRegisteredEvent = new ProfileRegisteredEvent(
                savedProfile.getUserId(),
                savedProfile.getId().toString()
        );
        profileRegisteredKafkaPublisher.publish(profileRegisteredEvent);

        logger.info("ProfileRegisteredEvent published to Kafka - userId: {}, profileId: {}",
                savedProfile.getUserId(), savedProfile.getId());

        return Optional.of(savedProfile);
    }

    // inherited javadoc
    @Override
    @Transactional
    public Optional<Profile> handle(UpdateProfileCommand command) {
        // Find profile by ID
        var profile = profileRepository.findById(command.profileId())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with ID: " + command.profileId()));

        // Check if username is being changed and if it's already taken by another profile
        if (!profile.getUsername().equals(command.username())) {
            var username = new Username(command.username());
            if (profileRepository.existsByUsername(username)) {
                throw new IllegalArgumentException("Username already exists: " + command.username());
            }
            profile.updateUsername(command.username());
        }

        // Update profile fields
        profile.updateName(command.firstName(), command.lastName());
        profile.updateProfileUrl(command.profileUrl());
        profile.updateProvider(command.provider());
        profile.updateCycle(command.cycle());

        // Save updated profile
        var savedProfile = profileRepository.save(profile);

        return Optional.of(savedProfile);
    }
}
