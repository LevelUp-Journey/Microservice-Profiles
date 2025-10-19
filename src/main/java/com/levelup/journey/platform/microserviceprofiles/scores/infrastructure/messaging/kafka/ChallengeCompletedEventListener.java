package com.levelup.journey.platform.microserviceprofiles.scores.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.commands.RecordScoreFromChallengeCommand;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.services.ScoreCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Challenge Completed Event Listener
 * Listens to challenge.completed events from Kafka and records scores
 */
@Component
public class ChallengeCompletedEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(ChallengeCompletedEventListener.class);
    private final ScoreCommandService scoreCommandService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ChallengeCompletedEventListener(
            ScoreCommandService scoreCommandService,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {
        this.scoreCommandService = scoreCommandService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "${spring.kafka.consumer.topic.challenge-completed:challenge.completed}",
            groupId = "${spring.kafka.consumer.group-id:profile-service-group}"
    )
    @SuppressWarnings("java:S1166") // Exception details logged separately
    public void handleChallengeCompleted(String message) {
        logger.info("Received challenge completion event: {}", message);

        try {
            // Parse JSON message
            JsonNode eventData = objectMapper.readTree(message);
            
            String userId = eventData.get("userId").asText();
            String challengeId = eventData.get("challengeId").asText();
            String challengeType = eventData.get("challengeType").asText();
            Integer points = eventData.get("points").asInt();

            // Create command
            var command = new RecordScoreFromChallengeCommand(
                    userId,
                    challengeId,
                    challengeType,
                    points
            );

            // Execute command
            var score = scoreCommandService.handle(command);

            if (score.isPresent()) {
                logger.info("Score recorded successfully for user: {} with points: {}", userId, points);
                
                // Publish score.points.awarded event
                publishScorePointsAwardedEvent(score.get());
            } else {
                logger.warn("Failed to record score for user: {}", userId);
            }

        } catch (Exception e) {
            logger.error("Error processing challenge completion event: {}", e.getMessage());
        }
    }

    /**
     * Publish score.points.awarded event to Kafka
     */
    private void publishScorePointsAwardedEvent(com.levelup.journey.platform.microserviceprofiles.scores.domain.model.aggregates.Score score) {
        try {
            String eventPayload = String.format(
                    "{\"userId\":\"%s\",\"points\":%d,\"source\":\"%s\",\"challengeId\":\"%s\",\"timestamp\":\"%s\"}",
                    score.getUserId(),
                    score.getPoints(),
                    score.getSource().name(),
                    score.getChallengeId(),
                    score.getCreatedAt()
            );

            kafkaTemplate.send("score.points.awarded", eventPayload);
            logger.info("Published score.points.awarded event for user: {}", score.getUserId());
        } catch (Exception e) {
            logger.error("Error publishing score.points.awarded event: {}", e.getMessage());
        }
    }
}
