package com.levelup.journey.platform.microserviceprofiles.suggestions.interfaces.rest;

import com.levelup.journey.platform.microserviceprofiles.suggestions.domain.model.queries.GetAllSuggestionsQuery;
import com.levelup.journey.platform.microserviceprofiles.suggestions.domain.services.SuggestionCommandService;
import com.levelup.journey.platform.microserviceprofiles.suggestions.domain.services.SuggestionQueryService;
import com.levelup.journey.platform.microserviceprofiles.suggestions.interfaces.rest.resources.CreateSuggestionResource;
import com.levelup.journey.platform.microserviceprofiles.suggestions.interfaces.rest.resources.SuggestionResource;
import com.levelup.journey.platform.microserviceprofiles.suggestions.interfaces.rest.transform.CreateSuggestionCommandFromResourceAssembler;
import com.levelup.journey.platform.microserviceprofiles.suggestions.interfaces.rest.transform.SuggestionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Suggestions Controller
 * REST API for suggestion operations
 */
@RestController
@RequestMapping(value = "/api/v1/suggestions")
@Tag(name = "Suggestions", description = "Suggestions management")
public class SuggestionsController {

    private final SuggestionCommandService suggestionCommandService;
    private final SuggestionQueryService suggestionQueryService;

    public SuggestionsController(
            SuggestionCommandService suggestionCommandService,
            SuggestionQueryService suggestionQueryService) {
        this.suggestionCommandService = suggestionCommandService;
        this.suggestionQueryService = suggestionQueryService;
    }

    /**
     * Create a new suggestion
     */
    @PostMapping
    @Operation(summary = "Create a new suggestion", description = "Creates a new suggestion in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Suggestion created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<SuggestionResource> createSuggestion(
            @Valid @RequestBody CreateSuggestionResource resource) {

        var command = CreateSuggestionCommandFromResourceAssembler.toCommandFromResource(resource);
        var suggestion = suggestionCommandService.handle(command);

        if (suggestion.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var suggestionResource = SuggestionResourceFromEntityAssembler.toResourceFromEntity(suggestion.get());
        return new ResponseEntity<>(suggestionResource, HttpStatus.CREATED);
    }

    /**
     * Get all suggestions
     */
    @GetMapping
    @Operation(summary = "Get all suggestions", description = "Retrieves all suggestions from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suggestions retrieved successfully")
    })
    public ResponseEntity<List<SuggestionResource>> getAllSuggestions() {

        var query = new GetAllSuggestionsQuery();
        var suggestions = suggestionQueryService.handle(query);

        var resources = suggestions.stream()
                .map(SuggestionResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }
}
