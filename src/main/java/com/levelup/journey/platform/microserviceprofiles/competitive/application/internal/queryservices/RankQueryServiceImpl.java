package com.levelup.journey.platform.microserviceprofiles.competitive.application.internal.queryservices;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.entities.Rank;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.services.RankQueryService;
import com.levelup.journey.platform.microserviceprofiles.competitive.infrastructure.persistence.jpa.repositories.RankRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Rank Query Service Implementation
 * Handles queries related to competitive ranks
 */
@Service
public class RankQueryServiceImpl implements RankQueryService {

    private static final Logger logger = LoggerFactory.getLogger(RankQueryServiceImpl.class);
    private final RankRepository rankRepository;

    public RankQueryServiceImpl(RankRepository rankRepository) {
        this.rankRepository = rankRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rank> getAllRanks() {
        logger.debug("Fetching all competitive ranks");
        return rankRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rank> getRankByName(String rankName) {
        logger.debug("Fetching rank by name: {}", rankName);

        try {
            var competitiveRank = com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.CompetitiveRank.valueOf(rankName.toUpperCase());
            return rankRepository.findByRankName(competitiveRank);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid rank name: {}", rankName);
            return Optional.empty();
        }
    }
}