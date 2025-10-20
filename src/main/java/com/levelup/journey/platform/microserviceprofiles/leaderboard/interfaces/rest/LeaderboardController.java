package com.levelup.journey.platform.microserviceprofiles.leaderboard.interfaces.rest;

import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.commands.RecalculateLeaderboardPositionsCommand;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.queries.GetLeaderboardQuery;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.queries.GetTop500Query;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.model.queries.GetUserPositionQuery;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.services.LeaderboardCommandService;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.domain.services.LeaderboardQueryService;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.interfaces.rest.resources.LeaderboardEntryResource;
import com.levelup.journey.platform.microserviceprofiles.leaderboard.interfaces.rest.transform.LeaderboardEntryResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Leaderboard Controller
 * REST API for leaderboard operations
 */
@RestController
@RequestMapping(value = "/api/v1/leaderboard")
@Tag(name = "Leaderboard", description = "Global leaderboard and rankings management")
public class LeaderboardController {

    private final LeaderboardCommandService leaderboardCommandService;
    private final LeaderboardQueryService leaderboardQueryService;

    public LeaderboardController(
            LeaderboardCommandService leaderboardCommandService,
            LeaderboardQueryService leaderboardQueryService) {
        this.leaderboardCommandService = leaderboardCommandService;
        this.leaderboardQueryService = leaderboardQueryService;
    }

    /**
     * Get global leaderboard with pagination
     */
    @GetMapping
    @Operation(summary = "Get leaderboard", description = "Retrieves paginated global leaderboard rankings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leaderboard retrieved successfully")
    })
    public ResponseEntity<List<LeaderboardEntryResource>> getLeaderboard(
            @Parameter(description = "Number of entries to return", example = "50")
            @RequestParam(defaultValue = "50") Integer limit,
            @Parameter(description = "Number of entries to skip", example = "0")
            @RequestParam(defaultValue = "0") Integer offset) {

        var query = new GetLeaderboardQuery(limit, offset);
        var entries = leaderboardQueryService.handle(query);

        var resources = entries.stream()
                .map(LeaderboardEntryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }

    /**
     * Get TOP 500 leaderboard
     */
    @GetMapping("/top500")
    @Operation(summary = "Get TOP 500", description = "Retrieves the top 500 users in the leaderboard")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TOP 500 retrieved successfully")
    })
    public ResponseEntity<List<LeaderboardEntryResource>> getTop500() {
        var query = new GetTop500Query();
        var entries = leaderboardQueryService.handle(query);

        var resources = entries.stream()
                .map(LeaderboardEntryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }

    /**
     * Get user's leaderboard position
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user position", description = "Retrieves a user's position in the global leaderboard")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User position retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found in leaderboard")
    })
    public ResponseEntity<LeaderboardEntryResource> getUserPosition(
            @Parameter(description = "User identifier", example = "user123")
            @PathVariable String userId) {

        var query = new GetUserPositionQuery(userId);
        var entry = leaderboardQueryService.handle(query);

        if (entry.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = LeaderboardEntryResourceFromEntityAssembler.toResourceFromEntity(entry.get());
        return ResponseEntity.ok(resource);
    }

    /**
     * Recalculate all leaderboard positions (Admin operation)
     */
    @PostMapping("/recalculate")
    @Operation(summary = "Recalculate leaderboard", description = "Recalculates all leaderboard positions based on current points (Admin operation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leaderboard recalculated successfully")
    })
    public ResponseEntity<String> recalculateLeaderboard() {
        var command = new RecalculateLeaderboardPositionsCommand();
        var updatedCount = leaderboardCommandService.handle(command);

        return ResponseEntity.ok(String.format("Leaderboard recalculated. Updated %d entries", updatedCount));
    }
}
