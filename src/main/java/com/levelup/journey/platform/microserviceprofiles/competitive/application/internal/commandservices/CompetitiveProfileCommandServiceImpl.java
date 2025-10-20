package com.levelup.journey.platform.microserviceprofiles.competitive.application.internal.commandservices;

import com.levelup.journey.platform.microserviceprofiles.competitive.application.internal.outboundservices.acl.ExternalScoresService;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.aggregates.CompetitiveProfile;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.CreateCompetitiveProfileCommand;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.RecalculateLeaderboardPositionsCommand;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.SyncCompetitiveProfileFromScoresCommand;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.UpdateCompetitivePointsCommand;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.CompetitiveRank;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.CompetitiveUserId;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.LeaderboardPosition;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.services.CompetitiveProfileCommandService;
import com.levelup.journey.platform.microserviceprofiles.competitive.infrastructure.persistence.jpa.repositories.CompetitiveProfileRepository;
import com.levelup.journey.platform.microserviceprofiles.competitive.infrastructure.persistence.jpa.repositories.RankRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Competitive Profile Command Service Implementation
 * Handles all command operations for competitive profiles
 */
@Service
public class CompetitiveProfileCommandServiceImpl implements CompetitiveProfileCommandService {

    private static final Logger logger = LoggerFactory.getLogger(CompetitiveProfileCommandServiceImpl.class);
    private final CompetitiveProfileRepository competitiveProfileRepository;
    private final RankRepository rankRepository;
    private final ExternalScoresService externalScoresService;

    public CompetitiveProfileCommandServiceImpl(
            CompetitiveProfileRepository competitiveProfileRepository,
            RankRepository rankRepository,
            ExternalScoresService externalScoresService) {
        this.competitiveProfileRepository = competitiveProfileRepository;
        this.rankRepository = rankRepository;
        this.externalScoresService = externalScoresService;
    }

    @Override
    @Transactional
    public Optional<CompetitiveProfile> handle(CreateCompetitiveProfileCommand command) {
        var userId = new CompetitiveUserId(command.userId());

        // Check if profile already exists
        if (competitiveProfileRepository.existsByUserId(userId)) {
            logger.warn("Competitive profile already exists for user: {}", command.userId());
            return competitiveProfileRepository.findByUserId(userId);
        }

        // Fetch initial points from Scores BC via ACL
        var totalPoints = externalScoresService.fetchTotalPointsByUserId(command.userId())
                .orElse(0);

        // Get bronze rank for new profiles
        var bronzeRank = rankRepository.findByRankName(CompetitiveRank.BRONZE)
                .orElseThrow(() -> new IllegalStateException("Bronze rank not found in database"));

        // Create competitive profile with fetched points and bronze rank
        var competitiveProfile = new CompetitiveProfile(command.userId(), totalPoints, bronzeRank);
        var savedProfile = competitiveProfileRepository.save(competitiveProfile);

        logger.info("Created competitive profile for user {} with {} points and rank {}",
                command.userId(), totalPoints, competitiveProfile.getCurrentRank().getRankName());

        return Optional.of(savedProfile);
    }

    @Override
    @Transactional
    public Optional<CompetitiveProfile> handle(UpdateCompetitivePointsCommand command) {
        var userId = new CompetitiveUserId(command.userId());

        var profile = competitiveProfileRepository.findByUserId(userId);

        if (profile.isEmpty()) {
            logger.warn("Competitive profile not found for user: {}", command.userId());
            return Optional.empty();
        }

        var competitiveProfile = profile.get();

        // Calculate new rank based on new points
        var newRankEnum = CompetitiveRank.fromPoints(command.newTotalPoints());
        var newRank = rankRepository.findByRankName(newRankEnum)
                .orElseThrow(() -> new IllegalStateException("Rank not found: " + newRankEnum));

        competitiveProfile.updatePoints(command, newRank);

        var updatedProfile = competitiveProfileRepository.save(competitiveProfile);

        logger.info("Updated competitive points for user {} to {} with rank {}",
                command.userId(), command.newTotalPoints(), updatedProfile.getCurrentRank().getRankName());

        return Optional.of(updatedProfile);
    }

    @Override
    @Transactional
    public Optional<CompetitiveProfile> handle(SyncCompetitiveProfileFromScoresCommand command) {
        var userId = new CompetitiveUserId(command.userId());

        // Fetch current total points from Scores BC
        var totalPoints = externalScoresService.fetchTotalPointsByUserId(command.userId());

        if (totalPoints.isEmpty()) {
            logger.warn("No scores found for user {} in Scores BC", command.userId());
            return Optional.empty();
        }

        // Calculate rank based on points
        var rankEnum = CompetitiveRank.fromPoints(totalPoints.get());
        var rank = rankRepository.findByRankName(rankEnum)
                .orElseThrow(() -> new IllegalStateException("Rank not found: " + rankEnum));

        // Get or create competitive profile
        var profile = competitiveProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    logger.info("Creating new competitive profile during sync for user: {}", command.userId());
                    var newProfile = new CompetitiveProfile(command.userId(), totalPoints.get(), rank);
                    return competitiveProfileRepository.save(newProfile);
                });

        // Sync points
        profile.syncPointsFromScores(totalPoints.get(), rank);
        var savedProfile = competitiveProfileRepository.save(profile);

        logger.info("Synchronized competitive profile for user {} with {} points and rank {}",
                command.userId(), totalPoints.get(), savedProfile.getCurrentRank().getRankName());

        return Optional.of(savedProfile);
    }

    @Override
    @Transactional
    public Integer handle(RecalculateLeaderboardPositionsCommand command) {
        logger.info("Starting leaderboard position recalculation");

        // Get TOP500 rank for position-based rank assignment
        var top500Rank = rankRepository.findByRankName(CompetitiveRank.TOP500)
                .orElseThrow(() -> new IllegalStateException("TOP500 rank not found in database"));

        // Get all profiles ordered by points (descending)
        List<CompetitiveProfile> allProfiles = competitiveProfileRepository.findAllOrderedByTotalPoints();

        int updatedCount = 0;
        int position = 1;

        for (CompetitiveProfile profile : allProfiles) {
            LeaderboardPosition newPosition = new LeaderboardPosition(position);

            // Update leaderboard position (this also handles TOP500 rank assignment for top 500)
            profile.updateLeaderboardPosition(newPosition, top500Rank);

            competitiveProfileRepository.save(profile);
            updatedCount++;
            position++;
        }

        logger.info("Leaderboard recalculation completed. Updated {} profiles", updatedCount);
        return updatedCount;
    }
}
