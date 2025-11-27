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
            topics = "${app.kafka.topics.challenge-completed:challenge.completed}",
            groupId = "${spring.kafka.consumer.group-id:profile-service-group}"
    )
    @SuppressWarnings("java:S1166") // Exception details logged separately
    public void handleChallengeCompleted(String message) {
        logger.info("📥 Received challenge completion message from Kafka");

        String userId = null;
        try {
            // Parse JSON message directly without DTO
            JsonNode eventData = objectMapper.readTree(message);

            userId = eventData.get("studentId").asText();
            String challengeId = eventData.get("challengeId").asText();
            String challengeType = "CHALLENGE"; // Default type as it's not in the event
            Integer points = eventData.get("experiencePointsEarned").asInt();

            // Check if challenge was already completed
            boolean alreadyCompleted = eventData.has("alreadyCompleted")
                    && eventData.get("alreadyCompleted").asBoolean();

            // Extract execution time if available (time taken to run the tests)
            Long executionTimeMs = eventData.has("executionTimeMs")
                    ? eventData.get("executionTimeMs").asLong()
                    : 0L;

            // Extract solution time if available (time taken to complete the challenge)
            Long solutionTimeSeconds = eventData.has("solutionTimeSeconds")
                    ? eventData.get("solutionTimeSeconds").asLong()
                    : 0L;

            logger.info("📥 Parsed event: studentId={}, challengeId={}, points={}, alreadyCompleted={}",
                    userId, challengeId, points, alreadyCompleted);

            // Skip score assignment if challenge was already completed
            if (alreadyCompleted) {
                logger.info("⏭️ Challenge {} already completed by user {}. Skipping score assignment.",
                        challengeId, userId);
                return;
            }

            logger.info("✅ Challenge completed by user {} with {} points, execution time: {} ms, solution time: {} s",
                    userId, points, executionTimeMs, solutionTimeSeconds);

            // Create command
            var command = new RecordScoreFromChallengeCommand(
                    userId,
                    challengeId,
                    challengeType,
                    points,
                    executionTimeMs,
                    solutionTimeSeconds
            );

            // Execute command
            var score = scoreCommandService.handle(command);

            if (score.isPresent()) {
                logger.info("Score recorded successfully for user: {} with points: {}, execution time: {} ms, solution time: {} s",
                        userId, points, executionTimeMs, solutionTimeSeconds);
            } else {
                logger.warn("Failed to record score for user: {}", userId);
            }

        } catch (Exception e) {
            String studentId = (userId != null) ? userId : "unknown";
            logger.error("❌ Error processing challenge completion event for studentId={}: {}",
                    studentId, e.getMessage(), e);
            // Re-lanzar para que el error handler lo maneje con reintentos
            throw new RuntimeException("Failed to process ChallengeCompleted event", e);
        }
    }
}
