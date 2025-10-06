package com.levelup.journey.platform.microserviceprofiles.profiles.domain.services;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.aggregates.ProfileRank;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.AddScoreCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.CreateProfileRankCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.SubtractScoreCommand;

import java.util.Optional;

public interface ProfileRankCommandService {
    Optional<ProfileRank> handle(CreateProfileRankCommand command);
    Optional<ProfileRank> handle(AddScoreCommand command);
    Optional<ProfileRank> handle(SubtractScoreCommand command);
}