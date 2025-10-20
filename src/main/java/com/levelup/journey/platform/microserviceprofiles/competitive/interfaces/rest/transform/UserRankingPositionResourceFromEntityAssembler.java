package com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.aggregates.CompetitiveProfile;
import com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.resources.UserRankingPositionResource;

/**
 * User Ranking Position Resource From Entity Assembler
 * Transforms CompetitiveProfile entity with calculated position to UserRankingPositionResource
 */
public class UserRankingPositionResourceFromEntityAssembler {

    /**
     * Transform CompetitiveProfile entity to UserRankingPositionResource
     *
     * @param entity The CompetitiveProfile entity
     * @param position Calculated leaderboard position
     * @return UserRankingPositionResource
     */
    public static UserRankingPositionResource toResourceFromEntity(CompetitiveProfile entity, Integer position) {
        return new UserRankingPositionResource(
                entity.getUserId(),
                position,
                entity.getTotalPoints(),
                entity.getCurrentRank().name()
        );
    }
}
