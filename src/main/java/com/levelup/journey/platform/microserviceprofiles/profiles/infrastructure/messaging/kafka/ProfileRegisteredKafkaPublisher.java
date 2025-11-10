package com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.messaging.kafka;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.events.ProfileRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Profile Registered Kafka Publisher
 * Service responsible for publishing ProfileRegisteredEvent to Kafka topic 'community-registration'.
 * This enables other microservices to react to new user registrations.
 */
@Service
@Slf4j
public class ProfileRegisteredKafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.community-registration}")
    private String communityRegistrationTopic;

    @Value("${app.kafka.enabled}")
    private boolean kafkaEnabled;

    public ProfileRegisteredKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a ProfileRegisteredEvent to the community-registration Kafka topic
     *
     * @param event The ProfileRegisteredEvent to publish
     */
    public void publish(ProfileRegisteredEvent event) {
        if (!kafkaEnabled) {
            log.warn("⚠️ Kafka is disabled. Skipping ProfileRegisteredEvent publication for userId: {}, profileId: {}",
                    event.getUserId(), event.getProfileId());
            return;
        }

        try {
            // Use userId as the message key for partitioning and ordering guarantees
            String messageKey = event.getUserId();

            log.info("📤 Publishing ProfileRegisteredEvent to topic '{}' - userId: {}, profileId: {}",
                    communityRegistrationTopic, event.getUserId(), event.getProfileId());

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                    communityRegistrationTopic,
                    messageKey,
                    event
            );

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("✅ ProfileRegisteredEvent published successfully - Topic: {}, Partition: {}, Offset: {}, userId: {}, profileId: {}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            event.getUserId(),
                            event.getProfileId());
                } else {
                    log.error("❌ Failed to publish ProfileRegisteredEvent - userId: {}, profileId: {}",
                            event.getUserId(), event.getProfileId(), ex);
                }
            });

        } catch (Exception e) {
            log.error("❌ Error publishing ProfileRegisteredEvent to Kafka - userId: {}, profileId: {}",
                    event.getUserId(), event.getProfileId(), e);
        }
    }
}
