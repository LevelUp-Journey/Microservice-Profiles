package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetAllProfilesQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetProfileByIdQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileCommandService;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileQueryService;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources.ProfileResource;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources.UpdateProfileResource;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.transform.UpdateProfileCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * ProfilesController
 */
@RestController
@RequestMapping(value = "/api/v1/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "Available Profile Endpoints")
public class ProfilesController {
    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;

    /**
     * Constructor
     * @param profileCommandService The {@link ProfileCommandService} instance
     * @param profileQueryService The {@link ProfileQueryService} instance
     */
    public ProfilesController(ProfileCommandService profileCommandService, ProfileQueryService profileQueryService) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
    }

    /**
     * Get a profile by ID
     * @param profileId The profile ID
     * @return A {@link ProfileResource} resource for the profile, or a not found response if the profile could not be found.
     */
    @GetMapping("/{profileId}")
    @Operation(summary = "Get a profile by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "404", description = "Profile not found")})
    public ResponseEntity<ProfileResource> getProfileById(@PathVariable UUID profileId) {
        var getProfileByIdQuery = new GetProfileByIdQuery(profileId);
        var profile = profileQueryService.handle(getProfileByIdQuery);
        if (profile.isEmpty()) return ResponseEntity.notFound().build();
        var profileEntity = profile.get();
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profileEntity);
        return ResponseEntity.ok(profileResource);
    }

    /**
     * Update a profile
     * @param profileId The profile ID
     * @param resource The {@link UpdateProfileResource} instance
     * @return A {@link ProfileResource} resource for the updated profile, or a not found/bad request response if the profile could not be updated.
     */
    @PutMapping("/{profileId}")
    @Operation(summary = "Update an existing profile", description = "Updates profile information including first name, last name, username, and profile URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or username already exists"),
            @ApiResponse(responseCode = "404", description = "Profile not found")})
    public ResponseEntity<ProfileResource> updateProfile(
            @PathVariable UUID profileId,
            @Valid @RequestBody UpdateProfileResource resource) {
        try {
            var updateProfileCommand = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(profileId, resource);
            var profile = profileCommandService.handle(updateProfileCommand);
            if (profile.isEmpty()) return ResponseEntity.notFound().build();
            var updatedProfile = profile.get();
            var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(updatedProfile);
            return ResponseEntity.ok(profileResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get a profile by User ID
     * @param userId The user ID from IAM service
     * @return A {@link ProfileResource} resource for the profile, or a not found response if the profile could not be found.
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get a profile by User ID", description = "Retrieves a profile associated with the given IAM user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "404", description = "Profile not found for the given user ID")})
    public ResponseEntity<ProfileResource> getProfileByUserId(@PathVariable String userId) {
        var getProfileByUserIdQuery = new GetProfileByUserIdQuery(new UserId(userId));
        var profile = profileQueryService.handle(getProfileByUserIdQuery);
        if (profile.isEmpty()) return ResponseEntity.notFound().build();
        var profileEntity = profile.get();
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profileEntity);
        return ResponseEntity.ok(profileResource);
    }

    /**
     * Get all profiles
     * @return A list of {@link ProfileResource} resources for all profiles, or a not found response if no profiles are found.
     */
    @GetMapping
    @Operation(summary = "Get all profiles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profiles found"),
            @ApiResponse(responseCode = "404", description = "Profiles not found")})
    public ResponseEntity<List<ProfileResource>> getAllProfiles() {
        var profiles = profileQueryService.handle(new GetAllProfilesQuery());
        if (profiles.isEmpty()) return ResponseEntity.notFound().build();
        var profileResources = profiles.stream()
                .map(ProfileResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(profileResources);
    }

}
