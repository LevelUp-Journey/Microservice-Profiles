package com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.Username;

public record GetProfileByUsernameQuery(Username username) {
}
