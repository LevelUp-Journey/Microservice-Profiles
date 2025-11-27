package com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.events.ProfileRegisteredEvent;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetAllProfilesForSyncQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileQueryService;
import com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.messaging.kafka.ProfileRegisteredKafkaPublisher;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.resources.ProfileSyncResource;
import com.levelup.journey.platform.microserviceprofiles.profiles.interfaces.rest.transform.ProfileSyncResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Profile Sync Controller
 * Provides endpoints for synchronizing all profiles with external services via Kafka.
 * This is useful when external services (like Community) need to rebuild their user database.
 */
@RestController
@RequestMapping(value = "/api/v1/profiles/sync", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Profile Sync", description = "Endpoints for profile synchronization with external services")
public class ProfileSyncController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileSyncController.class);

    private final ProfileQueryService profileQueryService;
    private final ProfileRegisteredKafkaPublisher profileRegisteredKafkaPublisher;

    /**
     * Constructor
     *
     * @param profileQueryService The {@link ProfileQueryService} instance
     * @param profileRegisteredKafkaPublisher The {@link ProfileRegisteredKafkaPublisher} instance
     */
    public ProfileSyncController(ProfileQueryService profileQueryService,
                                  ProfileRegisteredKafkaPublisher profileRegisteredKafkaPublisher) {
        this.profileQueryService = profileQueryService;
        this.profileRegisteredKafkaPublisher = profileRegisteredKafkaPublisher;
    }

    /**
     * Synchronize all profiles to Kafka
     * Retrieves all profiles from the database and publishes them to the community-registration Kafka topic.
     * This allows external services (like Community) to rebuild their user database after resets.
     *
     * @return A {@link List} of {@link ProfileSyncResource} representing all synchronized profiles
     */
    @PostMapping
    @Operation(
            summary = "Synchronize all profiles to Kafka",
            description = "Retrieves all profiles and publishes them to the community-registration Kafka topic. " +
                    "This endpoint is designed to help external services (like Community microservice) " +
                    "rebuild their user database after resets or data loss. " +
                    "Each profile is published as a ProfileRegisteredEvent to Kafka."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profiles synchronized successfully. Returns the list of all profiles that were published to Kafka."),
            @ApiResponse(responseCode = "204", description = "No profiles found to synchronize"),
            @ApiResponse(responseCode = "500", description = "Internal server error during synchronization")
    })
    public ResponseEntity<List<ProfileSyncResource>> syncAllProfiles() {
        try {
            logger.info("Starting profile synchronization...");

            // Get all profiles
            var getAllProfilesForSyncQuery = new GetAllProfilesForSyncQuery();
            var profiles = profileQueryService.handle(getAllProfilesForSyncQuery);

            if (profiles.isEmpty()) {
                logger.warn("No profiles found to synchronize");
                return ResponseEntity.noContent().build();
            }

            logger.info("Found {} profiles to synchronize", profiles.size());

            // Convert to resources and publish to Kafka
            var syncResources = profiles.stream()
                    .map(profile -> {
                        // Publish ProfileRegisteredEvent to Kafka for each profile
                        var profileRegisteredEvent = new ProfileRegisteredEvent(
                                profile.getUserId(),
                                profile.getId().toString(),
                                profile.getUsername(),
                                profile.getProfileUrl()
                        );
                        profileRegisteredKafkaPublisher.publish(profileRegisteredEvent);

                        // Return the resource for the response
                        return ProfileSyncResourceFromEntityAssembler.toResourceFromEntity(profile);
                    })
                    .toList();

            logger.info("Successfully synchronized {} profiles to Kafka", syncResources.size());

            return ResponseEntity.ok(syncResources);
        } catch (Exception e) {
            logger.error("Error during profile synchronization", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
