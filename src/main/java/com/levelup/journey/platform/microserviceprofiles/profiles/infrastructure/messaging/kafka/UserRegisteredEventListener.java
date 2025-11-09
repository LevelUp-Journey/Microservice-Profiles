package com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.CreateProfileFromUserCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.UpdateProfileCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileCommandService;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka Event Listener for User Registration Events
 * Listens to the user-registered topic and creates or updates profiles automatically
 * Only enabled when app.kafka.enabled=true
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class UserRegisteredEventListener {

    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;
    private final ObjectMapper objectMapper;

    public UserRegisteredEventListener(
            ProfileCommandService profileCommandService,
            ProfileQueryService profileQueryService,
            ObjectMapper objectMapper) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
        this.objectMapper = objectMapper;
    }

    /**
     * Listens to user registration events from IAM service
     * Topic: configured in app.kafka.topics.user-registered
     * Parsea JSON directamente sin DTOs (DDD compliant)
     *
     * @param message The JSON message from Kafka
     * @param topic The topic name
     * @param partition The partition number
     * @param offset The offset in the partition
     */
    @KafkaListener(
        topics = "${app.kafka.topics.user-registered}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserRegistered(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("📥 Received UserRegistered message from topic={}, partition={}, offset={}", topic, partition, offset);

        String userId = null;
        try {
            // Parse JSON directly without DTO (DDD compliant)
            JsonNode eventData = objectMapper.readTree(message);

            userId = eventData.get("userId").asText();
            String email = eventData.has("email") ? eventData.get("email").asText() : null;
            String firstName = eventData.has("firstName") ? eventData.get("firstName").asText() : "";
            String lastName = eventData.has("lastName") ? eventData.get("lastName").asText() : "";
            String profileUrl = eventData.has("profileUrl") && !eventData.get("profileUrl").isNull()
                    ? eventData.get("profileUrl").asText() : null;
            String provider = eventData.has("provider") ? eventData.get("provider").asText() : null;

            log.info("📥 Parsed event: userId={}, email={}, firstName={}, lastName={}",
                    userId, email, firstName, lastName);

            // Validate event data
            if (userId == null || userId.trim().isEmpty()) {
                log.error("❌ Invalid event: userId is null or empty");
                throw new IllegalArgumentException("userId cannot be null or empty");
            }

            var userIdVO = new UserId(userId);
            var existingProfile = profileQueryService.handle(new GetProfileByUserIdQuery(userIdVO));

            if (existingProfile.isPresent()) {
                // Update existing profile
                var profile = existingProfile.get();
                log.info("🔄 Updating existing profile for userId={}, profileId={}", userId, profile.getId());

                var updateCommand = new UpdateProfileCommand(
                        profile.getId(),
                        firstName,
                        lastName,
                        profile.getUsername(), // Keep existing username
                        profileUrl,
                        provider,
                        null // cycle not provided in event
                );

                var updatedProfile = profileCommandService.handle(updateCommand);
                if (updatedProfile.isPresent()) {
                    log.info("✅ Successfully updated profile for userId={}, profileId={}", userId, profile.getId());
                } else {
                    log.warn("⚠️ Failed to update profile for userId={}", userId);
                }
            } else {
                // Create new profile
                log.info("➕ Creating new profile for userId={}", userId);

                var createCommand = new CreateProfileFromUserCommand(
                        userId,
                        firstName,
                        lastName,
                        profileUrl,
                        provider
                );

                var profile = profileCommandService.handle(createCommand);
                if (profile.isPresent()) {
                    log.info("✅ Successfully created profile for userId={}, username={}, profileId={}",
                        userId, profile.get().getUsername(), profile.get().getId());
                } else {
                    log.warn("⚠️ Failed to create profile for userId={}", userId);
                }
            }
        } catch (Exception e) {
            String userIdForLog = (userId != null) ? userId : "unknown";
            log.error("❌ Error processing UserRegistered event for userId={}: {}",
                userIdForLog, e.getMessage(), e);
            // Re-lanzar para que el error handler lo maneje con reintentos
            throw new RuntimeException("Failed to process UserRegistered event", e);
        }
    }
}
