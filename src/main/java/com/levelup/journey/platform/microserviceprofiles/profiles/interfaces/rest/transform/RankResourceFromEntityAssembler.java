package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.entities.Rank;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources.RankResource;

public class RankResourceFromEntityAssembler {

    public static RankResource toResourceFromEntity(Rank entity) {
        return new RankResource(
            entity.getId(),
            entity.getName(),
            entity.getMinScore(),
            entity.getMaxScore(),
            entity.getRankOrder()
        );
    }
}