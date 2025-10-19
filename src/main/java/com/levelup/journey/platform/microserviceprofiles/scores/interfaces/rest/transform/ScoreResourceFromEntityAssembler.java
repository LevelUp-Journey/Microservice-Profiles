package com.levelup.journey.platform.microserviceprofiles.scores.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.aggregates.Score;
import com.levelup.journey.platform.microserviceprofiles.scores.interfaces.rest.resources.ScoreResource;

/**
 * Score Resource From Entity Assembler
 * Transforms Score entity to ScoreResource
 */
public class ScoreResourceFromEntityAssembler {
    
    public static ScoreResource toResourceFromEntity(Score entity) {
        return new ScoreResource(
                entity.getId(),
                entity.getUserId(),
                entity.getPoints(),
                entity.getSource().name(),
                entity.getChallengeId(),
                entity.getChallengeType(),
                entity.getCreatedAt()
        );
    }
}
