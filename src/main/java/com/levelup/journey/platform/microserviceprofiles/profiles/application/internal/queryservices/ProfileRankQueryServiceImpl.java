package com.levelup.journey.platform.microserviceprofiles.profiles.application.internal.queryservices;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.aggregates.ProfileRank;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.entities.ScoreAuditLog;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetLeaderboardQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetProfileRankByProfileIdQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetScoreHistoryByProfileIdQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileRankQueryService;
import com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.persistence.jpa.repositories.ProfileRankRepository;
import com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.persistence.jpa.repositories.ScoreAuditLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileRankQueryServiceImpl implements ProfileRankQueryService {

    private final ProfileRankRepository profileRankRepository;
    private final ScoreAuditLogRepository scoreAuditLogRepository;

    public ProfileRankQueryServiceImpl(ProfileRankRepository profileRankRepository,
                                     ScoreAuditLogRepository scoreAuditLogRepository) {
        this.profileRankRepository = profileRankRepository;
        this.scoreAuditLogRepository = scoreAuditLogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfileRank> handle(GetProfileRankByProfileIdQuery query) {
        return profileRankRepository.findByProfileId(query.profileId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileRank> handle(GetLeaderboardQuery query) {
        PageRequest pageRequest = PageRequest.of(0, query.limit());
        return profileRankRepository.findAll(pageRequest)
                .stream()
                .sorted((pr1, pr2) -> pr2.getCurrentScore().compareTo(pr1.getCurrentScore()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScoreAuditLog> handle(GetScoreHistoryByProfileIdQuery query) {
        return scoreAuditLogRepository.findScoreHistoryByProfileId(query.profileId());
    }
}