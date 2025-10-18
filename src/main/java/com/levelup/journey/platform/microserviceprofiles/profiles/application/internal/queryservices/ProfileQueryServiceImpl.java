package com.levelup.journey.platform.microserviceprofiles.profiles.application.internal.queryservices;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.aggregates.Profile;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetAllProfilesQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetProfileByUsernameQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetProfileByIdQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileQueryService;
import com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Profile Query Service Implementation
 */
@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {
    private final ProfileRepository profileRepository;

    /**
     * Constructor
     *
     * @param profileRepository The {@link ProfileRepository} instance
     */
    public ProfileQueryServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    // inherited javadoc
    @Override
    public Optional<Profile> handle(GetProfileByIdQuery query) {
        return profileRepository.findById(query.profileId());
    }

    // inherited javadoc
    @Override
    public Optional<Profile> handle(GetProfileByUsernameQuery query) {
        return profileRepository.findByUsername(query.username());
    }

    // inherited javadoc
    @Override
    public Optional<Profile> handle(GetProfileByUserIdQuery query) {
        return profileRepository.findByUserId(query.userId());
    }

    // inherited javadoc
    @Override
    public List<Profile> handle(GetAllProfilesQuery query) {
        return profileRepository.findAll();
    }
}
