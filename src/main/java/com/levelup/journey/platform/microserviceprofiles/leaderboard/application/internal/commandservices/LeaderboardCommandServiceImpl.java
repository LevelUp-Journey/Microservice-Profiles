package com.levelup.journey.platform.microserviceprofiles.leaderboard.application.internal.commandservices;

import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.aggregates.LeaderboardEntry;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.commands.RecalculateLeaderboardPositionsCommand;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.commands.UpdateLeaderboardEntryCommand;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.valueobjects.LeaderboardUserId;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.services.LeaderboardCommandService;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.infrastructure.persistence.jpa.repositories.LeaderboardEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Leaderboard Command Service Implementation
 * Handles all command operations for leaderboard
 */
@Service
public class LeaderboardCommandServiceImpl implements LeaderboardCommandService {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardCommandServiceImpl.class);
    private final LeaderboardEntryRepository leaderboardEntryRepository;

    public LeaderboardCommandServiceImpl(LeaderboardEntryRepository leaderboardEntryRepository) {
        this.leaderboardEntryRepository = leaderboardEntryRepository;
    }

    @Override
    @Transactional
    public Optional<LeaderboardEntry> handle(UpdateLeaderboardEntryCommand command) {
        var userId = new LeaderboardUserId(command.userId());

        // Check if entry already exists
        var existingEntry = leaderboardEntryRepository.findByUserId(userId);

        if (existingEntry.isPresent()) {
            // Update existing entry
            var entry = existingEntry.get();
            var newPosition = calculatePosition(command.totalPoints());
            entry.updatePointsAndPosition(command.totalPoints(), newPosition);

            var savedEntry = leaderboardEntryRepository.save(entry);
            logger.info("Updated leaderboard entry for user {} with {} points at position {}",
                    command.userId(), command.totalPoints(), newPosition);

            return Optional.of(savedEntry);
        } else {
            // Create new entry
            var newPosition = calculatePosition(command.totalPoints());
            var newEntry = new LeaderboardEntry(command, newPosition);

            var savedEntry = leaderboardEntryRepository.save(newEntry);
            logger.info("Created leaderboard entry for user {} with {} points at position {}",
                    command.userId(), command.totalPoints(), newPosition);

            return Optional.of(savedEntry);
        }
    }

    @Override
    @Transactional
    public Integer handle(RecalculateLeaderboardPositionsCommand command) {
        logger.info("Starting leaderboard position recalculation");

        // Get all entries ordered by points
        List<LeaderboardEntry> allEntries = leaderboardEntryRepository.findAllOrderedByPointsDesc();

        int updatedCount = 0;
        int position = 1;

        for (LeaderboardEntry entry : allEntries) {
            entry.updatePosition(position);
            leaderboardEntryRepository.save(entry);
            updatedCount++;
            position++;
        }

        logger.info("Leaderboard recalculation completed. Updated {} entries", updatedCount);
        return updatedCount;
    }

    /**
     * Calculate position for a given points value
     * Counts how many entries have more points
     */
    private Integer calculatePosition(Integer points) {
        var allEntries = leaderboardEntryRepository.findAllOrderedByPointsDesc();
        int position = 1;

        for (LeaderboardEntry entry : allEntries) {
            if (entry.getTotalPoints() > points) {
                position++;
            } else {
                break;
            }
        }

        return position;
    }
}
