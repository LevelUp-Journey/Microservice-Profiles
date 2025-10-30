package com.levelup.journey.platform.microserviceprofiles.leaderboard.application.acl;

import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.queries.GetTop500Query;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.queries.GetUserPositionQuery;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.services.LeaderboardQueryService;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.interfaces.acl.LeaderboardContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Leaderboard Context Facade Implementation
 * ACL implementation providing leaderboard data access to external bounded contexts
 */
@Service
public class LeaderboardContextFacadeImpl implements LeaderboardContextFacade {

    private final LeaderboardQueryService leaderboardQueryService;

    public LeaderboardContextFacadeImpl(LeaderboardQueryService leaderboardQueryService) {
        this.leaderboardQueryService = leaderboardQueryService;
    }

    @Override
    public Integer getUserPosition(String userId) {
        if (userId == null || userId.isBlank()) {
            return 0;
        }

        var query = new GetUserPositionQuery(userId);
        var entry = leaderboardQueryService.handle(query);

        return entry.map(leaderboardEntry -> leaderboardEntry.getPosition()).orElse(0);
    }

    @Override
    public List<String> getTopNUsers(Integer limit) {
        if (limit == null || limit < 1) {
            limit = 50;
        }

        var query = new com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.queries.GetLeaderboardQuery(limit, 0);
        var entries = leaderboardQueryService.handle(query);

        return entries.stream()
                .map(entry -> entry.getUserId())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getTop500UserIds() {
        var query = new GetTop500Query(500, 0);
        var entries = leaderboardQueryService.handle(query);

        return entries.stream()
                .map(entry -> entry.getUserId())
                .collect(Collectors.toList());
    }

    @Override
    public Boolean isUserInTop500(String userId) {
        if (userId == null || userId.isBlank()) {
            return false;
        }

        var query = new GetUserPositionQuery(userId);
        var entry = leaderboardQueryService.handle(query);

        return entry.map(leaderboardEntry -> leaderboardEntry.isTop500()).orElse(false);
    }
}
