package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.transform;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.entities.ScoreAuditLog;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources.ScoreAuditLogResource;

public class ScoreAuditLogResourceFromEntityAssembler {

    public static ScoreAuditLogResource toResourceFromEntity(ScoreAuditLog entity) {
        return new ScoreAuditLogResource(
            entity.getId(),
            entity.getProfileId(),
            entity.getScoreChange(),
            entity.getPreviousScore(),
            entity.getNewScore(),
            entity.getChangeReason(),
            entity.getChangeType().name(),
            entity.getExternalReferenceId(),
            entity.getCreatedAt()
        );
    }
}