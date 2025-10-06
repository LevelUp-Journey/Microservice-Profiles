package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddScoreResource(
    @NotNull(message = "Points cannot be null")
    @Positive(message = "Points must be positive")
    Integer points,

    @NotBlank(message = "Reason cannot be blank")
    String reason,

    String externalReferenceId
) {
}