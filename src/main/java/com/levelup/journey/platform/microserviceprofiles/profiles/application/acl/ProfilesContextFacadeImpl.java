package com.levelup.journey.platform.microserviceprofiles.profiles.application.acl;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.CreateProfileCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetProfileByUsernameQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.Username;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileCommandService;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileQueryService;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.acl.ProfilesContextFacade;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProfilesContextFacadeImpl implements ProfilesContextFacade {
    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;

    public ProfilesContextFacadeImpl(ProfileCommandService profileCommandService, ProfileQueryService profileQueryService) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
    }

    public UUID createProfile(
            String firstName,
            String lastName,
            String profileUrl) {
        var createProfileCommand = new CreateProfileCommand(
                firstName,
                lastName,
                profileUrl);
        var profile = profileCommandService.handle(createProfileCommand);
        return profile.isEmpty() ? null : profile.get().getId();
    }

    public UUID fetchProfileIdByUsername(String username) {
        var getProfileByUsernameQuery = new GetProfileByUsernameQuery(new Username(username));
        var profile = profileQueryService.handle(getProfileByUsernameQuery);
        return profile.isEmpty() ? null : profile.get().getId();
    }


}
