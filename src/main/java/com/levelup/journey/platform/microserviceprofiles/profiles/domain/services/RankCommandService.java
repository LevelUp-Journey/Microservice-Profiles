package com.levelup.journey.platform.microserviceprofiles.profiles.domain.services;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.InitializeRanksCommand;

public interface RankCommandService {
    void handle(InitializeRanksCommand command);
}