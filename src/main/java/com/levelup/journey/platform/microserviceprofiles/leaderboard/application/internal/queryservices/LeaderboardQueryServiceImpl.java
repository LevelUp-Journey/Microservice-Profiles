package com.levelup.journey.platform.microserviceprofiles.leaderboard.application.internal.queryservices;

import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.aggregates.LeaderboardEntry;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.queries.GetLeaderboardQuery;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.queries.GetTop500Query;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.queries.GetUserPositionQuery;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.valueobjects.LeaderboardUserId;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.services.LeaderboardQueryService;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.infrastructure.persistence.jpa.repositories.LeaderboardEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Leaderboard Query Service Implementation
 * Handles all query operations for leaderboard
 */
@Service
public class LeaderboardQueryServiceImpl implements LeaderboardQueryService {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardQueryServiceImpl.class);
    private final LeaderboardEntryRepository leaderboardEntryRepository;

    public LeaderboardQueryServiceImpl(LeaderboardEntryRepository leaderboardEntryRepository) {
        this.leaderboardEntryRepository = leaderboardEntryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaderboardEntry> handle(GetLeaderboardQuery query) {
        logger.debug("Fetching leaderboard with limit: {} and offset: {}", query.limit(), query.offset());

        var pageable = PageRequest.of(
                query.offset() / query.limit(), // page number
                query.limit()                   // page size
        );

        return leaderboardEntryRepository.findTopEntriesByPoints(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LeaderboardEntry> handle(GetUserPositionQuery query) {
        var userId = new LeaderboardUserId(query.userId());
        var entry = leaderboardEntryRepository.findByUserId(userId);

        if (entry.isEmpty()) {
            logger.debug("Leaderboard entry not found for user: {}", query.userId());
        }

        return entry;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaderboardEntry> handle(GetTop500Query query) {
        logger.debug("Fetching top 500 leaderboard entries (limit: {}, offset: {})", query.limit(), query.offset());

        var pageable = PageRequest.of(
                query.offset() / query.limit(), // page number
                query.limit()                   // page size
        );
        return leaderboardEntryRepository.findTop500(pageable);
    }
}
