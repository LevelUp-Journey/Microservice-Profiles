package com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.messaging.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Consumer Configuration with SASL_SSL support for Azure Event Hubs
 * Configures Kafka consumer for receiving user registration events from IAM service
 */
@Configuration
@EnableKafka
@Slf4j
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.properties.security.protocol:PLAINTEXT}")
    private String securityProtocol;

    @Value("${spring.kafka.properties.sasl.mechanism:#{null}}")
    private String saslMechanism;

    @Value("${spring.kafka.properties.sasl.jaas.config:#{null}}")
    private String saslJaasConfig;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        
        // Conexión básica
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        
        // Seguridad (PLAINTEXT para local, SASL_SSL para Azure Event Hubs)
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);

        // Solo agregar configuración SASL si está presente (para Azure Event Hubs)
        if (saslMechanism != null && !saslMechanism.isEmpty()) {
            props.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        }
        if (saslJaasConfig != null && !saslJaasConfig.isEmpty()) {
            props.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
        }
        
        // Deserializers - Usar StringDeserializer para permitir que cada listener parsee manualmente
        // Esto permite tener diferentes tipos de eventos en diferentes topics
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        log.info("✅ Kafka Consumer configured - Protocol: {}, Bootstrap: {}, GroupId: {}",
            securityProtocol, bootstrapServers, groupId);
        
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        
        // Manejo de errores con reintentos
        factory.setCommonErrorHandler(new DefaultErrorHandler(
            (record, exception) -> {
                log.error("❌ Error processing Kafka record after retries: key={}, value={}, topic={}, partition={}, offset={}", 
                    record.key(), record.value(), record.topic(), record.partition(), record.offset(), exception);
            },
            new FixedBackOff(1000L, 3L) // 3 reintentos con 1 segundo de espera entre cada uno
        ));
        
        log.info("✅ Kafka Listener Container Factory configured with error handling");
        
        return factory;
    }
}
