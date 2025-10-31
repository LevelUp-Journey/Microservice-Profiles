package com.levelup.journey.platform.microserviceprofiles.scores.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.model.commands.RecordScoreFromChallengeCommand;
import com.levelup.journey.platform.microserviceprofiles.scores.domain.services.ScoreCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Challenge Completed Event Listener
 * Listens to challenge.completed events from Kafka and records scores
 */
@Component
public class ChallengeCompletedEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(ChallengeCompletedEventListener.class);
    private final ScoreCommandService scoreCommandService;
    private final ObjectMapper objectMapper;

    public ChallengeCompletedEventListener(
            ScoreCommandService scoreCommandService,
            ObjectMapper objectMapper) {
        this.scoreCommandService = scoreCommandService;
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

            String userId = eventData.get("studentId").asText();
            String challengeId = eventData.get("challengeId").asText();
            String challengeType = "CHALLENGE"; // Default type as it's not in the event
            Integer points = eventData.get("experiencePointsEarned").asInt();

            // Extract execution time if available (time taken to complete the challenge)
            Long executionTimeMs = eventData.has("executionTimeMs")
                    ? eventData.get("executionTimeMs").asLong()
                    : 0L;

            logger.info("Challenge completed by user {} with {} points in {} ms",
                    userId, points, executionTimeMs);

            // Create command
            var command = new RecordScoreFromChallengeCommand(
                    userId,
                    challengeId,
                    challengeType,
                    points,
                    executionTimeMs
            );

            // Execute command
            var score = scoreCommandService.handle(command);

            if (score.isPresent()) {
                logger.info("Score recorded successfully for user: {} with points: {} and execution time: {} ms",
                        userId, points, executionTimeMs);
            } else {
                logger.warn("Failed to record score for user: {}", userId);
            }

        } catch (Exception e) {
            logger.error("Error processing challenge completion event: {}", e.getMessage());
        }
    }
}
