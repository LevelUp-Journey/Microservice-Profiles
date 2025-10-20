package com.levelup.journey.platform.microserviceprofiles.competitive.application.internal.queryservices;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.aggregates.CompetitiveProfile;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries.GetAllCompetitiveProfilesQuery;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries.GetCompetitiveProfileByUserIdQuery;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries.GetUsersByRankQuery;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.CompetitiveUserId;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.services.CompetitiveProfileQueryService;
import com.levelup.journey.platform.microserviceprofiles.competitive.infrastructure.persistence.jpa.repositories.CompetitiveProfileRepository;
import com.levelup.journey.platform.microserviceprofiles.competitive.infrastructure.persistence.jpa.repositories.RankRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public List<CompetitiveProfile> handle(GetUsersByRankQuery query) {
        logger.debug("Fetching users with rank: {}", query.rank());

        // Find the rank entity by enum value
        var rankEntity = rankRepository.findByRankName(query.rank())
                .orElseThrow(() -> new IllegalArgumentException("Rank not found: " + query.rank()));

        return competitiveProfileRepository.findByCurrentRank(rankEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetitiveProfile> handle(GetAllCompetitiveProfilesQuery query) {
        logger.debug("Fetching all competitive profiles");
        return competitiveProfileRepository.findAll();
    }
}
