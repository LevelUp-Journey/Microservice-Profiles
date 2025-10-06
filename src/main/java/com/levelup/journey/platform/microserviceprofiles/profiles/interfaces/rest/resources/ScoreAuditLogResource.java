package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources;

import java.util.Date;
import java.util.UUID;

public record ScoreAuditLogResource(
    UUID id,
    UUID profileId,
    Integer scoreChange,
    Integer previousScore,
    Integer newScore,
    String changeReason,
    String changeType,
    String externalReferenceId,
    Date createdAt
) {
}