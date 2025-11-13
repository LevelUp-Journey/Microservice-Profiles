package com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.messaging.kafka;

import com.levelup.journey.platform.microserviceprofiles.profiles.domain.model.events.ProfileUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Publishes ProfileUpdatedEvent instances to the community-profile-updated topic.
 */
@Service
@Slf4j
public class ProfileUpdatedKafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.community-profile-updated}")
    private String communityProfileUpdatedTopic;

    @Value("${app.kafka.enabled}")
    private boolean kafkaEnabled;

    public ProfileUpdatedKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ProfileUpdatedEvent event) {
        if (!kafkaEnabled) {
            log.warn("⚠️ Kafka disabled. Skipping ProfileUpdatedEvent for userId: {}", event.getUserId());
            return;
        }

        try {
            var messageKey = event.getUserId();
            log.info("📤 Publishing ProfileUpdatedEvent to '{}' - userId: {}, profileId: {}",
                    communityProfileUpdatedTopic, event.getUserId(), event.getProfileId());

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                    communityProfileUpdatedTopic,
                    messageKey,
                    event
            );

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("✅ ProfileUpdatedEvent published - Topic: {}, Partition: {}, Offset: {}, userId: {}, profileId: {}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            event.getUserId(),
                            event.getProfileId());
                } else {
                    log.error("❌ Failed to publish ProfileUpdatedEvent - userId: {}, profileId: {}",
                            event.getUserId(), event.getProfileId(), ex);
                }
            });
        } catch (Exception e) {
            log.error("❌ Error publishing ProfileUpdatedEvent - userId: {}, profileId: {}",
                    event.getUserId(), event.getProfileId(), e);
        }
    }
}
