package com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.messaging.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Kafka Connection Test
 * Tests Kafka connectivity when the application starts
 * Only runs when Kafka is enabled
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConnectionTest {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaConnectionTest(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void testKafkaConnection() {
        log.info("🧪 Testing Kafka connection...");
        
        try {
            var testMessage = Map.of(
                "test", "connection", 
                "service", "profile-service",
                "timestamp", System.currentTimeMillis()
            );
            
            var future = kafkaTemplate.send("test-topic", "connection-test", testMessage);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("✅ Kafka connection test SUCCESS - Message sent to topic: {}, partition: {}, offset: {}", 
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                } else {
                    log.error("❌ Kafka connection test FAILED: {}", ex.getMessage());
                }
            });
            
        } catch (Exception e) {
            log.error("❌ Cannot test Kafka connection: {}", e.getMessage(), e);
        }
    }
}
