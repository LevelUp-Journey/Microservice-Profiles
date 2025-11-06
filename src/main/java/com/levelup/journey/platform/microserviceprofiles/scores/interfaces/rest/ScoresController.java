package com.levelup.journey.platform.microserviceprofiles.scores.interfaces.rest;

import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.queries.GetAllScoresQuery;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.queries.GetScoresByUserIdQuery;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.queries.GetTotalPointsByUserIdQuery;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.valueobjects.ScoreUserId;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.services.ScoreQueryService;
import com.levelup.journey.platform.microserviceprofiles.scores.interfaces.rest.resources.ScoreResource;
import com.levelup.journey.platform.microserviceprofiles.scores.interfaces.rest.transform.ScoreResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Scores Controller
 * REST API endpoints for score management
 */
@RestController
@RequestMapping(value = "/api/v1/scores", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Scores", description = "Score Management Endpoints")
public class ScoresController {
    
    private final ScoreQueryService scoreQueryService;

    public ScoresController(ScoreQueryService scoreQueryService) {
        this.scoreQueryService = scoreQueryService;
    }

    /**
     * Get all scores for a specific user
     * @param userId The user ID
     * @return List of scores for the user
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get scores by user ID", description = "Retrieves all score records for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scores found"),
            @ApiResponse(responseCode = "404", description = "No scores found for the user")
    })
    public ResponseEntity<List<ScoreResource>> getScoresByUserId(@PathVariable String userId) {
        var query = new GetScoresByUserIdQuery(new ScoreUserId(userId));
        var scores = scoreQueryService.handle(query);
        
        if (scores.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        var scoreResources = scores.stream()
                .map(ScoreResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        return ResponseEntity.ok(scoreResources);
    }

    /**
     * Get total points for a specific user
     * @param userId The user ID
     * @return Total points
     */
    @GetMapping("/user/{userId}/total")
    @Operation(summary = "Get total points by user ID", description = "Calculates total points accumulated by a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total points calculated"),
            @ApiResponse(responseCode = "404", description = "User has no scores")
    })
    public ResponseEntity<Integer> getTotalPointsByUserId(@PathVariable String userId) {
        var query = new GetTotalPointsByUserIdQuery(new ScoreUserId(userId));
        var totalPoints = scoreQueryService.handle(query);
        
        return ResponseEntity.ok(totalPoints);
    }

    /**
     * Get all scores in the system
     * @return List of all scores
     */
    @GetMapping
    @Operation(summary = "Get all scores", description = "Retrieves all score records in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scores found"),
            @ApiResponse(responseCode = "404", description = "No scores found")
    })
    public ResponseEntity<List<ScoreResource>> getAllScores() {
        var scores = scoreQueryService.handle(new GetAllScoresQuery());
        
        if (scores.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        var scoreResources = scores.stream()
                .map(ScoreResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        
        return ResponseEntity.ok(scoreResources);
    }
}
