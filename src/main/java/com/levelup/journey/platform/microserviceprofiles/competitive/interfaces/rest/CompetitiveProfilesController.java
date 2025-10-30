package com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest;

import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.commands.SyncCompetitiveProfileFromScoresCommand;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries.GetCompetitiveProfileByUserIdQuery;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.queries.GetUsersByRankQuery;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.model.valueobjects.CompetitiveRank;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.services.CompetitiveProfileCommandService;
import com.levelup.journey.platform.microserviceprofiles.competitive.domain.services.CompetitiveProfileQueryService;
import com.levelup.journey.platform.microserviceprofiles.competitive.infrastructure.persistence.jpa.repositories.CompetitiveProfileRepository;
import com.levelup.journey.platform.microserviceprofiles.competitive.infrastructure.persistence.jpa.repositories.RankRepository;
import com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.resources.CompetitiveProfileResource;
import com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.resources.UsersByRankResponse;
import com.levelup.journey.platform.microserviceprofiles.competitive.interfaces.rest.transform.CompetitiveProfileResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Competitive Profiles Controller
 * REST API for competitive profile operations
 */
@RestController
@RequestMapping(value = "/api/v1/competitive/profiles")
@Tag(name = "Competitive Profiles", description = "Competitive profiles management")
public class CompetitiveProfilesController {

    private final CompetitiveProfileCommandService competitiveProfileCommandService;
    private final CompetitiveProfileQueryService competitiveProfileQueryService;
    private final CompetitiveProfileRepository competitiveProfileRepository;
    private final RankRepository rankRepository;
    private final CompetitiveProfileResourceFromEntityAssembler assembler;

    public CompetitiveProfilesController(
            CompetitiveProfileCommandService competitiveProfileCommandService,
            CompetitiveProfileQueryService competitiveProfileQueryService,
            CompetitiveProfileRepository competitiveProfileRepository,
            RankRepository rankRepository,
            CompetitiveProfileResourceFromEntityAssembler assembler) {
        this.competitiveProfileCommandService = competitiveProfileCommandService;
        this.competitiveProfileQueryService = competitiveProfileQueryService;
        this.competitiveProfileRepository = competitiveProfileRepository;
        this.rankRepository = rankRepository;
        this.assembler = assembler;
    }

    /**
     * Get competitive profile by user ID
     * NOTE: Competitive profiles are created automatically when users earn scores.
     * Use the sync endpoint if a profile needs to be manually synchronized.
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get competitive profile by user ID", description = "Retrieves a user's competitive profile including rank and points")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Competitive profile found"),
            @ApiResponse(responseCode = "404", description = "Competitive profile not found")
    })
    public ResponseEntity<CompetitiveProfileResource> getCompetitiveProfileByUserId(
            @Parameter(description = "User identifier", example = "user123")
            @PathVariable String userId) {

        var query = new GetCompetitiveProfileByUserIdQuery(userId);
        var profile = competitiveProfileQueryService.handle(query);

        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = assembler.toResourceFromEntity(profile.get());
        return ResponseEntity.ok(resource);
    }

    /**
     * Get users by rank
     */
    @GetMapping("/rank/{rank}")
    @Operation(summary = "Get users by rank", description = "Retrieves all users with a specific competitive rank and total count")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid rank")
    })
    public ResponseEntity<UsersByRankResponse> getUsersByRank(
            @Parameter(description = "Competitive rank", example = "DIAMOND")
            @PathVariable String rank) {

        try {
            var competitiveRank = CompetitiveRank.valueOf(rank.toUpperCase());
            var query = new GetUsersByRankQuery(competitiveRank);
            var profiles = competitiveProfileQueryService.handle(query);

            var resources = profiles.stream()
                    .map(assembler::toResourceFromEntity)
                    .toList();

            // Get the rank entity and count users with this rank
            var rankEntity = rankRepository.findByRankName(competitiveRank)
                    .orElseThrow(() -> new IllegalArgumentException("Rank not found: " + competitiveRank));

            var totalUsers = competitiveProfileRepository.countByCurrentRank(rankEntity);
            var response = new UsersByRankResponse(resources, totalUsers);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Sync user's competitive profile with Scores BC
     */
    @PostMapping("/user/{userId}/sync")
    @Operation(summary = "Sync competitive profile", description = "Synchronizes a user's competitive profile with their current scores from Scores BC")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile synchronized successfully"),
            @ApiResponse(responseCode = "404", description = "No scores found for user")
    })
    public ResponseEntity<CompetitiveProfileResource> syncCompetitiveProfile(
            @Parameter(description = "User identifier", example = "user123")
            @PathVariable String userId) {

        var command = new SyncCompetitiveProfileFromScoresCommand(userId);
        var profile = competitiveProfileCommandService.handle(command);

        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = assembler.toResourceFromEntity(profile.get());
        return ResponseEntity.ok(resource);
    }

}
