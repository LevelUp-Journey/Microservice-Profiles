package com.levelup.journey.platform.microserviceprofiles.profiles.domain.services;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.aggregates.ProfileRank;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.entities.ScoreAuditLog;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetLeaderboardQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetProfileRankByProfileIdQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetScoreHistoryByProfileIdQuery;

import java.util.List;
import java.util.Optional;

public interface ProfileRankQueryService {
    Optional<ProfileRank> handle(GetProfileRankByProfileIdQuery query);
    List<ProfileRank> handle(GetLeaderboardQuery query);
    List<ScoreAuditLog> handle(GetScoreHistoryByProfileIdQuery query);
}