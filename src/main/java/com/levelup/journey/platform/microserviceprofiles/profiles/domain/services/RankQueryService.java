package com.levelup.journey.platform.microserviceprofiles.profiles.domain.services;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.entities.Rank;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetAllRanksQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetRankByNameQuery;

import java.util.List;
import java.util.Optional;

public interface RankQueryService {
    List<Rank> handle(GetAllRanksQuery query);
    Optional<Rank> handle(GetRankByNameQuery query);
}