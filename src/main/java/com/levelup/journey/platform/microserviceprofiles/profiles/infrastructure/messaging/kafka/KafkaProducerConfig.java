package com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.messaging.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Producer Configuration with SASL_SSL support for Azure Event Hubs
 * Configures Kafka producer for sending profile registration events to community-registration topic
 */
@Configuration
@Slf4j
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.properties.security.protocol:PLAINTEXT}")
    private String securityProtocol;

    @Value("${spring.kafka.properties.sasl.mechanism:#{null}}")
    private String saslMechanism;

    @Value("${spring.kafka.properties.sasl.jaas.config:#{null}}")
    private String saslJaasConfig;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();

        // Basic connection configuration
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Security configuration (PLAINTEXT for local, SASL_SSL for Azure Event Hubs)
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);

        // Only add SASL configuration if present (for Azure Event Hubs)
        if (saslMechanism != null && !saslMechanism.isEmpty()) {
            props.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        }
        if (saslJaasConfig != null && !saslJaasConfig.isEmpty()) {
            props.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
        }

        // Serializers - Use JsonSerializer for automatic object serialization
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Producer performance and reliability settings
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // Wait for all replicas to acknowledge
        props.put(ProducerConfig.RETRIES_CONFIG, 3); // Retry failed sends up to 3 times
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 2000); // Max time to block for metadata
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 2000); // Request timeout
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 3000); // Total delivery timeout

        // JSON serializer configuration
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false); // Don't add type headers

        log.info("✅ Kafka Producer configured - Protocol: {}, Bootstrap: {}",
            securityProtocol, bootstrapServers);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Define community-registration topic bean
     * Spring Kafka will automatically create the topic if it doesn't exist
     * (if auto.create.topics.enable=true in Kafka broker)
     *
     * @return NewTopic configuration for community-registration
     */
    @Bean
    public NewTopic communityRegistrationTopic(@Value("${app.kafka.topics.community-registration}") String topicName) {
        return TopicBuilder.name(topicName)
                .partitions(3)  // 3 partitions for load distribution
                .replicas(1)    // 1 replica for development (adjust for production)
                .build();
    }
}
