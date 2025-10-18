package com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.CreateProfileFromUserCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Kafka Event Listener for User Registration Events
 * Listens to the iam.user.registered topic and creates profiles automatically
 */
@Component
public class UserRegisteredEventListener {

    private static final Logger logger = LoggerFactory.getLogger(UserRegisteredEventListener.class);

    private final ProfileCommandService profileCommandService;
    private final ObjectMapper objectMapper;

    public UserRegisteredEventListener(ProfileCommandService profileCommandService, ObjectMapper objectMapper) {
        this.profileCommandService = profileCommandService;
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

            // Create command
            var command = new CreateProfileFromUserCommand(
                    userId,
                    firstName != null ? firstName : "",
                    lastName != null ? lastName : "",
                    profileUrl
            );

            // Handle command to create profile
            var profile = profileCommandService.handle(command);

            if (profile.isPresent()) {
                logger.info("Successfully created profile for user ID: {} with username: {}",
                        userId, profile.get().getUsername());
            } else {
                logger.warn("Failed to create profile for user ID: {}", userId);
            }

        } catch (IllegalArgumentException e) {
            logger.warn("Profile creation skipped: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing user registration event: {}", e.getMessage(), e);
        }
    }
}
