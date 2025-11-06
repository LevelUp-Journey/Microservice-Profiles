package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.UserId;

public record GetProfileByUserIdQuery(UserId userId) {
}
