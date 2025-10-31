package com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.CreateProfileFromUserCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.UpdateProfileCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileCommandService;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Kafka Event Listener for User Registration Events
 * Listens to the iam.user.registered topic and creates or updates profiles automatically
 */
@Component
public class UserRegisteredEventListener {

    private static final Logger logger = LoggerFactory.getLogger(UserRegisteredEventListener.class);

    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;
    private final ObjectMapper objectMapper;

    public UserRegisteredEventListener(ProfileCommandService profileCommandService, ProfileQueryService profileQueryService, ObjectMapper objectMapper) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
        this.objectMapper = objectMapper;
    }

    /**
     * Listens to user registration events from IAM service
     * Topic: iam.user.registered
     *
     * @param message The JSON message from Kafka
     */
    @KafkaListener(topics = "${spring.kafka.consumer.topic.user-registered}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleUserRegistered(String message) {
        try {
            logger.info("Received user registration event: {}", message);

            // Parse JSON message
            @SuppressWarnings("unchecked")
            Map<String, Object> event = objectMapper.readValue(message, Map.class);

            // Extract data from event
            String userId = (String) event.get("userId");
            String firstName = (String) event.get("firstName");
            String lastName = (String) event.get("lastName");
            String profileUrl = (String) event.get("profileUrl");
            String provider = (String) event.get("provider");

            // Log extracted data for debugging
            logger.info("Extracted from Kafka event - userId: {}, firstName: '{}', lastName: '{}', profileUrl: '{}', provider: '{}'",
                    userId, firstName, lastName, profileUrl, provider);

            var userIdVO = new UserId(userId);
            var existingProfile = profileQueryService.handle(new GetProfileByUserIdQuery(userIdVO));

            if (existingProfile.isPresent()) {
                // Update existing profile
                var profile = existingProfile.get();
                var updateCommand = new UpdateProfileCommand(
                        profile.getId(),
                        firstName != null ? firstName : "",
                        lastName != null ? lastName : "",
                        profile.getUsername(), // Keep existing username
                        profileUrl,
                        provider,
                        null // cycle not provided in event
                );
                var updatedProfile = profileCommandService.handle(updateCommand);
                if (updatedProfile.isPresent()) {
                    logger.info("Successfully updated profile for user ID: {} with profileUrl: '{}'",
                            userId, profileUrl);
                } else {
                    logger.warn("Failed to update profile for user ID: {}", userId);
                }
            } else {
                // Create new profile
                var createCommand = new CreateProfileFromUserCommand(
                        userId,
                        firstName != null ? firstName : "",
                        lastName != null ? lastName : "",
                        profileUrl,
                        provider
                );
                var profile = profileCommandService.handle(createCommand);
                if (profile.isPresent()) {
                    logger.info("Successfully created profile for user ID: {} with username: {}",
                            userId, profile.get().getUsername());
                } else {
                    logger.warn("Failed to create profile for user ID: {}", userId);
                }
            }

        } catch (Exception e) {
            logger.error("Error processing user registration event: {}", e.getMessage(), e);
        }
    }
}
