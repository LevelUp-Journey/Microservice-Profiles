package com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.messaging.kafka;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.CreateProfileFromUserCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.commands.UpdateProfileCommand;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.valueobjects.UserId;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileCommandService;
import com.levelup.journey.platform.microserviceprofiles.profiles.domain.services.ProfileQueryService;
import com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.messaging.dto.UserRegisteredEvent;
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

    public UserRegisteredEventListener(
            ProfileCommandService profileCommandService, 
            ProfileQueryService profileQueryService) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
    }

    /**
     * Listens to user registration events from IAM service
     * Topic: configured in app.kafka.topics.user-registered
     *
     * @param event The UserRegisteredEvent from Kafka
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
            @Payload UserRegisteredEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("📥 Received UserRegistered event: userId={}, email={}, username={}, topic={}, partition={}, offset={}", 
            event.getUserId(), event.getEmail(), event.getUsername(), topic, partition, offset);
        
        try {
            // Validate event data
            if (event.getUserId() == null || event.getUserId().trim().isEmpty()) {
                log.error("❌ Invalid event: userId is null or empty");
                throw new IllegalArgumentException("userId cannot be null or empty");
            }

            var userIdVO = new UserId(event.getUserId());
            var existingProfile = profileQueryService.handle(new GetProfileByUserIdQuery(userIdVO));

            if (existingProfile.isPresent()) {
                // Update existing profile
                var profile = existingProfile.get();
                log.info("🔄 Updating existing profile for userId={}, profileId={}", 
                    event.getUserId(), profile.getId());
                
                var updateCommand = new UpdateProfileCommand(
                        profile.getId(),
                        event.getFirstName() != null ? event.getFirstName() : "",
                        event.getLastName() != null ? event.getLastName() : "",
                        profile.getUsername(), // Keep existing username
                        event.getProfileUrl(),
                        event.getProvider(),
                        null // cycle not provided in event
                );
                
                var updatedProfile = profileCommandService.handle(updateCommand);
                if (updatedProfile.isPresent()) {
                    log.info("✅ Successfully updated profile for userId={}, profileId={}", 
                        event.getUserId(), profile.getId());
                } else {
                    log.warn("⚠️ Failed to update profile for userId={}", event.getUserId());
                }
            } else {
                // Create new profile
                log.info("➕ Creating new profile for userId={}", event.getUserId());
                
                var createCommand = new CreateProfileFromUserCommand(
                        event.getUserId(),
                        event.getFirstName() != null ? event.getFirstName() : "",
                        event.getLastName() != null ? event.getLastName() : "",
                        event.getProfileUrl(),
                        event.getProvider()
                );
                
                var profile = profileCommandService.handle(createCommand);
                if (profile.isPresent()) {
                    log.info("✅ Successfully created profile for userId={}, username={}, profileId={}", 
                        event.getUserId(), profile.get().getUsername(), profile.get().getId());
                } else {
                    log.warn("⚠️ Failed to create profile for userId={}", event.getUserId());
                }
            }
        } catch (Exception e) {
            log.error("❌ Error processing UserRegistered event for userId={}: {}", 
                event.getUserId(), e.getMessage(), e);
            // Re-lanzar para que el error handler lo maneje con reintentos
            throw new RuntimeException("Failed to process UserRegistered event", e);
        }
    }
}
