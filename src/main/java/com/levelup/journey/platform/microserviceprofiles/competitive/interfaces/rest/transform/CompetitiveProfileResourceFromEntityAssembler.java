package com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.aggregates.CompetitiveProfile;
import com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.resources.CompetitiveProfileResource;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.interfaces.acl.LeaderboardContextFacade;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.acl.ProfilesContextFacade;
import org.springframework.stereotype.Component;

/**
 * Competitive Profile Resource From Entity Assembler
 * Transforms CompetitiveProfile entity to CompetitiveProfileResource
 */
@Component
public class CompetitiveProfileResourceFromEntityAssembler {

    private final LeaderboardContextFacade leaderboardContextFacade;
    private final ProfilesContextFacade profilesContextFacade;

    public CompetitiveProfileResourceFromEntityAssembler(
            LeaderboardContextFacade leaderboardContextFacade,
            ProfilesContextFacade profilesContextFacade) {
        this.leaderboardContextFacade = leaderboardContextFacade;
        this.profilesContextFacade = profilesContextFacade;
    }

    /**
     * Transform CompetitiveProfile entity to CompetitiveProfileResource
     *
     * @param entity The CompetitiveProfile entity
     * @return CompetitiveProfileResource
     */
    public CompetitiveProfileResource toResourceFromEntity(CompetitiveProfile entity) {
        var currentRankEntity = entity.getCurrentRank();
        var currentRankEnum = currentRankEntity.getRankName();
        var nextRank = currentRankEnum.getNextRank();
        var pointsNeeded = nextRank != null ? nextRank.getMinimumPoints() - entity.getTotalPoints() : 0;

        // Get username via Profiles ACL facade
        var username = profilesContextFacade.getUsernameByUserId(entity.getUserId());

        // Get leaderboard position via Leaderboard ACL facade
        var leaderboardPosition = leaderboardContextFacade.getUserPosition(entity.getUserId());
        var position = leaderboardPosition > 0 ? leaderboardPosition : null;

        return new CompetitiveProfileResource(
                entity.getId().toString(),
                entity.getUserId(),
                username,
                entity.getTotalPoints(),
                currentRankEnum.name(),
                nextRank != null ? nextRank.name() : null,
                pointsNeeded,
                position
        );
    }
}
