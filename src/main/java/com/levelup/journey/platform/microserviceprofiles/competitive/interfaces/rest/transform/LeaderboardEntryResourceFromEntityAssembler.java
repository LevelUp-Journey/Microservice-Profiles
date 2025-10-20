package com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.aggregates.CompetitiveProfile;
import com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.resources.LeaderboardEntryResource;

/**
 * Leaderboard Entry Resource From Entity Assembler
 * Transforms CompetitiveProfile entity to LeaderboardEntryResource
 */
public class LeaderboardEntryResourceFromEntityAssembler {

    /**
     * Transform CompetitiveProfile entity to LeaderboardEntryResource
     *
     * @param entity The CompetitiveProfile entity
     * @return LeaderboardEntryResource
     */
    public static LeaderboardEntryResource toResourceFromEntity(CompetitiveProfile entity) {
        return new LeaderboardEntryResource(
                entity.getLeaderboardPosition(),
                entity.getUserId(),
                entity.getTotalPoints(),
                entity.getCurrentRank().name()
        );
    }
}
