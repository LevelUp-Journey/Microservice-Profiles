package com.levelup.journey.platform.microserviceprofiles.competitive.application.internal.commandservices;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.SeedRanksCommand;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.entities.Rank;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.CompetitiveRank;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.services.RankCommandService;
import com.levelup.journey.platform.microserviceprofiles.competitive.infrastructure.persistence.jpa.repositories.RankRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * Rank Command Service Implementation
 * Handles rank-related commands including seeding
 */
@Service
public class RankCommandServiceImpl implements RankCommandService {

    private static final Logger logger = LoggerFactory.getLogger(RankCommandServiceImpl.class);
    private final RankRepository rankRepository;

    public RankCommandServiceImpl(RankRepository rankRepository) {
        this.rankRepository = rankRepository;
    }

    @Override
    @Transactional
    public void handle(SeedRanksCommand command) {
        logger.info("🎯 Starting rank data seeding from CompetitiveRank enum");

        Arrays.stream(CompetitiveRank.values()).forEach(competitiveRank -> {
            if (!rankRepository.existsByRankName(competitiveRank)) {
                var rank = new Rank(competitiveRank, competitiveRank.getMinimumPoints());
                rankRepository.save(rank);
                logger.info("   ✓ Seeded rank: {} ({}+ points)", 
                    competitiveRank.name(), 
                    competitiveRank.getMinimumPoints());
            } else {
                logger.debug("   → Rank {} already exists, skipping", competitiveRank.name());
            }
        });

        long totalRanks = rankRepository.count();
        logger.info("✅ Rank seeding completed. Total ranks in database: {}", totalRanks);
    }
}
