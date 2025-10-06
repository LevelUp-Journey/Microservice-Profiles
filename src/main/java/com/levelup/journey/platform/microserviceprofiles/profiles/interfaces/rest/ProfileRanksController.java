package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.CreateProfileRankCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetLeaderboardQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetProfileRankByProfileIdQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetScoreHistoryByProfileIdQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileRankCommandService;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileRankQueryService;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources.AddScoreResource;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources.ProfileRankResource;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources.ScoreAuditLogResource;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources.SubtractScoreResource;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.transform.AddScoreCommandFromResourceAssembler;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.transform.ProfileRankResourceFromAggregateAssembler;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.transform.ScoreAuditLogResourceFromEntityAssembler;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.transform.SubtractScoreCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/profile-ranks")
@Tag(name = "Profile Ranks", description = "Profile ranking and scoring management endpoints")
public class ProfileRanksController {

    private final ProfileRankCommandService profileRankCommandService;
    private final ProfileRankQueryService profileRankQueryService;

    public ProfileRanksController(ProfileRankCommandService profileRankCommandService,
                                ProfileRankQueryService profileRankQueryService) {
        this.profileRankCommandService = profileRankCommandService;
        this.profileRankQueryService = profileRankQueryService;
    }

    @PostMapping("/{profileId}")
    @Operation(summary = "Create profile rank", description = "Creates a new profile rank for the specified profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Profile rank created successfully"),
        @ApiResponse(responseCode = "409", description = "Profile rank already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProfileRankResource> createProfileRank(
            @Parameter(description = "Profile ID") @PathVariable UUID profileId) {

        var command = new CreateProfileRankCommand(profileId);
        var profileRankOpt = profileRankCommandService.handle(command);

        return profileRankOpt.map(profileRank -> {
            var resource = ProfileRankResourceFromAggregateAssembler.toResourceFromAggregate(profileRank, "Bronze");
            return ResponseEntity.status(HttpStatus.CREATED).body(resource);
        }).orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @GetMapping("/{profileId}")
    @Operation(summary = "Get profile rank", description = "Retrieves the rank information for a specific profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile rank retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Profile rank not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProfileRankResource> getProfileRank(
            @Parameter(description = "Profile ID") @PathVariable UUID profileId) {

        var query = new GetProfileRankByProfileIdQuery(profileId);
        var profileRankOpt = profileRankQueryService.handle(query);

        return profileRankOpt.map(profileRank -> {
            // For simplicity, we'll determine rank name by score
            var rankName = new com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.Score(profileRank.getCurrentScore()).determineRankName();
            var resource = ProfileRankResourceFromAggregateAssembler.toResourceFromAggregate(profileRank, rankName);
            return ResponseEntity.ok(resource);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{profileId}/add-score")
    @Operation(summary = "Add score to profile", description = "Adds points to a profile's score")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Score added successfully"),
        @ApiResponse(responseCode = "404", description = "Profile rank not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProfileRankResource> addScore(
            @Parameter(description = "Profile ID") @PathVariable UUID profileId,
            @Valid @RequestBody AddScoreResource resource) {

        var command = AddScoreCommandFromResourceAssembler.toCommandFromResource(profileId, resource);
        var profileRankOpt = profileRankCommandService.handle(command);

        return profileRankOpt.map(profileRank -> {
            var rankName = new com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.Score(profileRank.getCurrentScore()).determineRankName();
            var responseResource = ProfileRankResourceFromAggregateAssembler.toResourceFromAggregate(profileRank, rankName);
            return ResponseEntity.ok(responseResource);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{profileId}/subtract-score")
    @Operation(summary = "Subtract score from profile", description = "Subtracts points from a profile's score")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Score subtracted successfully"),
        @ApiResponse(responseCode = "404", description = "Profile rank not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProfileRankResource> subtractScore(
            @Parameter(description = "Profile ID") @PathVariable UUID profileId,
            @Valid @RequestBody SubtractScoreResource resource) {

        var command = SubtractScoreCommandFromResourceAssembler.toCommandFromResource(profileId, resource);
        var profileRankOpt = profileRankCommandService.handle(command);

        return profileRankOpt.map(profileRank -> {
            var rankName = new com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.Score(profileRank.getCurrentScore()).determineRankName();
            var responseResource = ProfileRankResourceFromAggregateAssembler.toResourceFromAggregate(profileRank, rankName);
            return ResponseEntity.ok(responseResource);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{profileId}/score-history")
    @Operation(summary = "Get score history", description = "Retrieves the score change history for a profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Score history retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ScoreAuditLogResource>> getScoreHistory(
            @Parameter(description = "Profile ID") @PathVariable UUID profileId) {

        var query = new GetScoreHistoryByProfileIdQuery(profileId);
        var scoreHistory = profileRankQueryService.handle(query);
        var resources = scoreHistory.stream()
                .map(ScoreAuditLogResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Get leaderboard", description = "Retrieves the top profiles by score")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Leaderboard retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ProfileRankResource>> getLeaderboard(
            @Parameter(description = "Maximum number of profiles to return (default: 10, max: 100)")
            @RequestParam(defaultValue = "10") Integer limit) {

        var query = new GetLeaderboardQuery(limit);
        var leaderboard = profileRankQueryService.handle(query);
        var resources = leaderboard.stream()
                .map(profileRank -> {
                    var rankName = new com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.Score(profileRank.getCurrentScore()).determineRankName();
                    return ProfileRankResourceFromAggregateAssembler.toResourceFromAggregate(profileRank, rankName);
                })
                .toList();
        return ResponseEntity.ok(resources);
    }
}