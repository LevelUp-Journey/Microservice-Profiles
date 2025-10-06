package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.InitializeRanksCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetAllRanksQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetRankByNameQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.RankCommandService;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.RankQueryService;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources.RankResource;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.transform.RankResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/ranks")
@Tag(name = "Ranks", description = "Available ranks management endpoints")
public class RanksController {

    private final RankCommandService rankCommandService;
    private final RankQueryService rankQueryService;

    public RanksController(RankCommandService rankCommandService, RankQueryService rankQueryService) {
        this.rankCommandService = rankCommandService;
        this.rankQueryService = rankQueryService;
    }

    @PostMapping("/initialize")
    @Operation(summary = "Initialize default ranks", description = "Creates the default ranking system with 7 ranks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ranks initialized successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> initializeRanks() {
        var command = new InitializeRanksCommand();
        rankCommandService.handle(command);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Get all ranks", description = "Retrieves all available ranks ordered by rank order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ranks retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<RankResource>> getAllRanks() {
        var query = new GetAllRanksQuery();
        var ranks = rankQueryService.handle(query);
        var rankResources = ranks.stream()
                .map(RankResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(rankResources);
    }

    @GetMapping("/{rankName}")
    @Operation(summary = "Get rank by name", description = "Retrieves a specific rank by its name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rank retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Rank not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<RankResource> getRankByName(@PathVariable String rankName) {
        var query = new GetRankByNameQuery(rankName);
        var rankOpt = rankQueryService.handle(query);
        return rankOpt.map(rank -> ResponseEntity.ok(RankResourceFromEntityAssembler.toResourceFromEntity(rank)))
                .orElse(ResponseEntity.notFound().build());
    }
}