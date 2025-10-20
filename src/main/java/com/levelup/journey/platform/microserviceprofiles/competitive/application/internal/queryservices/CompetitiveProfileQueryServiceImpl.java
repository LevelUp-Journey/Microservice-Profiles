package com.levelup.journey.platform.microserviceprofiles.competitive.application.internal.queryservices;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.aggregates.CompetitiveProfile;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries.GetAllCompetitiveProfilesQuery;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries.GetCompetitiveProfileByUserIdQuery;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries.GetLeaderboardQuery;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries.GetUserRankingPositionQuery;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries.GetUsersByRankQuery;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.CompetitiveUserId;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.services.CompetitiveProfileQueryService;
import com.levelup.journey.platform.microserviceprofiles.competitive.infrastructure.persistence.jpa.repositories.CompetitiveProfileRepository;
import com.levelup.journey.platform.microserviceprofiles.competitive.infrastructure.persistence.jpa.repositories.RankRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Competitive Profile Query Service Implementation
 * Handles all query operations for competitive profiles
 */
@Service
public class CompetitiveProfileQueryServiceImpl implements CompetitiveProfileQueryService {

    private static final Logger logger = LoggerFactory.getLogger(CompetitiveProfileQueryServiceImpl.class);
    private final CompetitiveProfileRepository competitiveProfileRepository;
    private final RankRepository rankRepository;

    public CompetitiveProfileQueryServiceImpl(
            CompetitiveProfileRepository competitiveProfileRepository,
            RankRepository rankRepository) {
        this.competitiveProfileRepository = competitiveProfileRepository;
        this.rankRepository = rankRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompetitiveProfile> handle(GetCompetitiveProfileByUserIdQuery query) {
        var userId = new CompetitiveUserId(query.userId());
        var profile = competitiveProfileRepository.findByUserId(userId);

        if (profile.isEmpty()) {
            logger.debug("Competitive profile not found for user: {}", query.userId());
        }

        return profile;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetitiveProfile> handle(GetLeaderboardQuery query) {
        logger.debug("Fetching leaderboard with limit: {} and offset: {}", query.limit(), query.offset());

        var pageable = PageRequest.of(
                query.offset() / query.limit(), // page number
                query.limit()                   // page size
        );

        return competitiveProfileRepository.findTopByOrderByTotalPointsDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetitiveProfile> handle(GetUsersByRankQuery query) {
        logger.debug("Fetching users with rank: {}", query.rank());

        // Find the rank entity by enum value
        var rankEntity = rankRepository.findByRankName(query.rank())
                .orElseThrow(() -> new IllegalArgumentException("Rank not found: " + query.rank()));

        return competitiveProfileRepository.findByCurrentRank(rankEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Integer> handle(GetUserRankingPositionQuery query) {
        var userId = new CompetitiveUserId(query.userId());
        var profileOpt = competitiveProfileRepository.findByUserId(userId);

        if (profileOpt.isEmpty()) {
            logger.debug("Cannot determine position - profile not found for user: {}", query.userId());
            return Optional.empty();
        }

        var profile = profileOpt.get();

        // If leaderboard position is already calculated, return it
        if (profile.getLeaderboardPosition() != null) {
            return Optional.of(profile.getLeaderboardPosition());
        }

        // Otherwise, calculate position on-the-fly
        Integer userPoints = profile.getTotalPoints();
        Long profilesWithMorePoints = competitiveProfileRepository.countProfilesWithMorePoints(userPoints);

        // Add 1 because position is 1-indexed, and also account for ties
        Long profilesWithSamePointsButEarlier = competitiveProfileRepository
                .countProfilesWithSamePointsButEarlier(userPoints, profile.getCreatedAt());

        Integer position = (int) (profilesWithMorePoints + profilesWithSamePointsButEarlier + 1);

        logger.debug("Calculated position {} for user {}", position, query.userId());
        return Optional.of(position);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetitiveProfile> handle(GetAllCompetitiveProfilesQuery query) {
        logger.debug("Fetching all competitive profiles");
        return competitiveProfileRepository.findAll();
    }
}
