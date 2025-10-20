package com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.entities.Rank;
import com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.resources.RankResource;

/**
 * Rank Resource From Entity Assembler
 * Transforms Rank entity to RankResource
 */
public class RankResourceFromEntityAssembler {

    /**
     * Transform Rank entity to RankResource
     *
     * @param entity The Rank entity
     * @return RankResource
     */
    public static RankResource toResourceFromEntity(Rank entity) {
        return new RankResource(
                entity.getId().toString(),
                entity.getRankName().name(),
                entity.getMinimumPoints()
        );
    }
}