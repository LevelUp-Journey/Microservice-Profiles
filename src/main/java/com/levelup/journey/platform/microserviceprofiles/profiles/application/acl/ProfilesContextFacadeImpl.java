package com.levelup.journey.platform.microserviceprofiles.profiles.application.acl;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileQueryService;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.acl.ProfilesContextFacade;
import org.springframework.stereotype.Service;

/**
 * Profiles Context Facade Implementation
 * Implements ACL interface for accessing Profile information from other bounded contexts
 */
@Service
public class ProfilesContextFacadeImpl implements ProfilesContextFacade {

    private final ProfileQueryService profileQueryService;

    public ProfilesContextFacadeImpl(ProfileQueryService profileQueryService) {
        this.profileQueryService = profileQueryService;
    }

    @Override
    public String getUsernameByUserId(String userId) {
        var query = new GetProfileByUserIdQuery(new UserId(userId));
        var profile = profileQueryService.handle(query);
        return profile.map(p -> p.getUsername()).orElse(null);
    }

    @Override
    public String getProfileIdByUserId(String userId) {
        var query = new GetProfileByUserIdQuery(new UserId(userId));
        var profile = profileQueryService.handle(query);
        return profile.map(p -> p.getId().toString()).orElse(null);
    }
}
