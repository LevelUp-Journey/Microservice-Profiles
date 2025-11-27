package com.levelup.journey.platform.microserviceprofiles.leaderboard.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.aggregates.LeaderboardEntry;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.interfaces.rest.resources.LeaderboardEntryResource;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.acl.ProfilesContextFacade;
import org.springframework.stereotype.Component;

/**
 * Leaderboard Entry Resource From Entity Assembler
 * Transforms LeaderboardEntry entity to LeaderboardEntryResource
 */
@Component
public class LeaderboardEntryResourceFromEntityAssembler {

    private final ProfilesContextFacade profilesContextFacade;

    public LeaderboardEntryResourceFromEntityAssembler(ProfilesContextFacade profilesContextFacade) {
        this.profilesContextFacade = profilesContextFacade;
    }

    /**
     * Transform LeaderboardEntry entity to LeaderboardEntryResource
     *
     * @param entity The LeaderboardEntry entity
     * @return LeaderboardEntryResource
     */
    public LeaderboardEntryResource toResourceFromEntity(LeaderboardEntry entity) {
        // Get username via Profiles ACL facade
        var username = profilesContextFacade.getUsernameByUserId(entity.getUserId());

        return new LeaderboardEntryResource(
                entity.getId().toString(),
                entity.getUserId(),
                username,
                entity.getTotalPoints(),
                entity.getPosition(),
                entity.isTop500()
        );
    }
}
