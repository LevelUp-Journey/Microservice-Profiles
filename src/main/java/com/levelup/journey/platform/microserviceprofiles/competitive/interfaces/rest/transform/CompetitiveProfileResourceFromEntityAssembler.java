package com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.aggregates.CompetitiveProfile;
import com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.resources.CompetitiveProfileResource;

/**
 * Competitive Profile Resource From Entity Assembler
 * Transforms CompetitiveProfile entity to CompetitiveProfileResource
 */
public class CompetitiveProfileResourceFromEntityAssembler {

    /**
     * Transform CompetitiveProfile entity to CompetitiveProfileResource
     *
     * @param entity The CompetitiveProfile entity
     * @return CompetitiveProfileResource
     */
    public static CompetitiveProfileResource toResourceFromEntity(CompetitiveProfile entity) {
        var currentRankEntity = entity.getCurrentRank();
        var currentRankEnum = currentRankEntity.getRankName();
        var nextRank = currentRankEnum.getNextRank();
        var pointsNeeded = nextRank != null ? nextRank.getMinimumPoints() - entity.getTotalPoints() : 0;

        return new CompetitiveProfileResource(
                entity.getId().toString(),
                entity.getUserId(),
                entity.getTotalPoints(),
                currentRankEnum.name(),
                entity.getLeaderboardPosition(),
                nextRank != null ? nextRank.name() : null,
                pointsNeeded,
                entity.isTop500()
        );
    }
}
