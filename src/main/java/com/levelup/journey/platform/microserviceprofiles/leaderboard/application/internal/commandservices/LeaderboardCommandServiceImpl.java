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

        Integer oldPosition = null;
        if (existingEntry.isPresent()) {
            oldPosition = existingEntry.get().getPosition();

            // Update existing entry - first calculate the new position
            var entry = existingEntry.get();

            // Update points only first (keep current position temporarily)
            entry.updatePointsAndPosition(command.totalPoints(), entry.getPosition());
            var savedEntry = leaderboardEntryRepository.save(entry);

            // Recalculate position with tie-breaking after save
            var calculatedPosition = leaderboardEntryRepository.calculatePositionForUser(userId);
            savedEntry.updatePosition(calculatedPosition.intValue());
            savedEntry = leaderboardEntryRepository.save(savedEntry);

            logger.info("Updated leaderboard entry for user {} with {} points. Position changed: {} → {}",
                    command.userId(), command.totalPoints(), oldPosition, calculatedPosition);

            // Recalculate positions for ALL other users to maintain consistency
            recalculateAllPositions();

            return Optional.of(savedEntry);
        } else {
            // Create new entry - assign last position initially
            var totalEntries = leaderboardEntryRepository.countTotalEntries();
            var initialPosition = totalEntries.intValue() + 1;
            var newEntry = new LeaderboardEntry(command, initialPosition);

            var savedEntry = leaderboardEntryRepository.save(newEntry);

            // Recalculate position with tie-breaking after save
            var calculatedPosition = leaderboardEntryRepository.calculatePositionForUser(userId);
            savedEntry.updatePosition(calculatedPosition.intValue());
            savedEntry = leaderboardEntryRepository.save(savedEntry);

            logger.info("Created leaderboard entry for user {} with {} points at position {}",
                    command.userId(), command.totalPoints(), calculatedPosition);

            // Recalculate positions for ALL other users to maintain consistency
            recalculateAllPositions();

            return Optional.of(savedEntry);
        }
    }

    @Override
    @Transactional
    public Integer handle(RecalculateLeaderboardPositionsCommand command) {
        logger.info("Starting leaderboard position recalculation");
        return recalculateAllPositions();
    }

    /**
     * Recalculates positions for ALL leaderboard entries
     * Uses the tie-breaking logic: higher points first, then earlier created_at
     *
     * This ensures:
     * - No duplicate positions
     * - Correct ordering after any update
     * - Users who gain points push others down
     *
     * @return Number of entries updated
     */
    private Integer recalculateAllPositions() {
        // Get all entries
        List<LeaderboardEntry> allEntries = leaderboardEntryRepository.findAll();

        int updatedCount = 0;

        for (LeaderboardEntry entry : allEntries) {
            // Calculate correct position using tie-breaking query
            var userIdVO = new LeaderboardUserId(entry.getUserId());
            var calculatedPosition = leaderboardEntryRepository.calculatePositionForUser(userIdVO);

            if (calculatedPosition != null) {
                Integer currentPosition = entry.getPosition();
                Integer newPosition = calculatedPosition.intValue();

                // Only update if position changed
                if (!newPosition.equals(currentPosition)) {
                    entry.updatePosition(newPosition);
                    leaderboardEntryRepository.save(entry);
                    updatedCount++;
                    logger.debug("Updated position for user {}: {} → {}",
                            entry.getUserId(), currentPosition, newPosition);
                }
            }
        }

        logger.info("Leaderboard recalculation completed. Updated {} entries", updatedCount);
        return updatedCount;
    }
}
