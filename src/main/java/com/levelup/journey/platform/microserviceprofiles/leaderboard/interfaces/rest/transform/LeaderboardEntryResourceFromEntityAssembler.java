package com.levelup.journey.platform.microserviceprofiles.leaderboard.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.aggregates.LeaderboardEntry;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.interfaces.rest.resources.LeaderboardEntryResource;

/**
 * Leaderboard Entry Resource From Entity Assembler
 * Transforms LeaderboardEntry entity to LeaderboardEntryResource
 */
public class LeaderboardEntryResourceFromEntityAssembler {

    /**
     * Transform LeaderboardEntry entity to LeaderboardEntryResource
     *
     * @param entity The LeaderboardEntry entity
     * @return LeaderboardEntryResource
     */
    public static LeaderboardEntryResource toResourceFromEntity(LeaderboardEntry entity) {
        return new LeaderboardEntryResource(
                entity.getId().toString(),
                entity.getUserId(),
                entity.getTotalPoints(),
                entity.getPosition(),
                entity.getTotalTimeToAchievePointsMs(),
                entity.isTop500()
        );
    }
}
