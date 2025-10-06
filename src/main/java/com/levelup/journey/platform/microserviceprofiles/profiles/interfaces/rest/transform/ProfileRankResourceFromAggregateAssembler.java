package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.aggregates.ProfileRank;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.entities.Rank;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources.ProfileRankResource;

public class ProfileRankResourceFromAggregateAssembler {

    public static ProfileRankResource toResourceFromAggregate(ProfileRank aggregate, Rank rank) {
        return new ProfileRankResource(
            aggregate.getId(),
            aggregate.getProfileId(),
            aggregate.getRankId(),
            rank.getName(),
            aggregate.getCurrentScore(),
            aggregate.getTotalScoreAccumulated(),
            aggregate.getCreatedAt(),
            aggregate.getUpdatedAt()
        );
    }

    public static ProfileRankResource toResourceFromAggregate(ProfileRank aggregate, String rankName) {
        return new ProfileRankResource(
            aggregate.getId(),
            aggregate.getProfileId(),
            aggregate.getRankId(),
            rankName,
            aggregate.getCurrentScore(),
            aggregate.getTotalScoreAccumulated(),
            aggregate.getCreatedAt(),
            aggregate.getUpdatedAt()
        );
    }
}