# 🔧 Kafka Configuration - Profile Service

## 📋 Resumen de Cambios

Se ha corregido completamente la configuración de Kafka para soportar **SASL_SSL con Azure Event Hubs**.

---

## ✅ Cambios Implementados

### 1. **Dependencias Maven (`pom.xml`)**

Se añadió la dependencia crítica de `kafka-clients`:

```xml
<!-- Kafka Clients - IMPORTANTE para SASL_SSL con Azure Event Hubs -->
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
</dependency>

<!-- Jackson para manejo de fechas -->
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
```

### 2. **Configuración corregida (`application.yml`)**

#### ❌ Antes (INCORRECTO):
```yaml
consumer:
  topic:  # ⚠️ NO ES ESTÁNDAR
    user-registered: ${KAFKA_TOPIC_USER_REGISTERED}
```

#### ✅ Ahora (CORRECTO):
```yaml
spring:
  kafka:
    consumer:
      # ... configuración estándar ...
      properties:
        spring:
          json:
            trusted:
              packages: '*'
            type:
              mapping: 'userRegistered:com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.messaging.dto.UserRegisteredEvent'

# Topics en configuración custom de la app
app:
  kafka:
    enabled: ${KAFKA_ENABLED:true}
    topics:
      user-registered: ${KAFKA_TOPIC_USER_REGISTERED:iam.user.registered}
      challenge-completed: ${KAFKA_TOPIC_CHALLENGE_COMPLETED:challenge.completed}
```

### 3. **Nuevo DTO: `UserRegisteredEvent`**

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegisteredEvent {
    private String userId;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String profileUrl;
    private String provider;
    private Instant timestamp;
}
```

### 4. **KafkaConsumerConfig con SASL_SSL**

Configuración completa con:
- ✅ SASL_SSL authentication
- ✅ JsonDeserializer correctamente configurado
- ✅ Error handler con reintentos (3 reintentos, 1 segundo entre cada uno)
- ✅ Trusted packages: `*`
- ✅ Type mapping automático

```java
// Seguridad SASL_SSL para Azure Event Hubs
props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
props.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
props.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);

// CRÍTICO: Configuración del JsonDeserializer
props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.levelup...UserRegisteredEvent");
```

### 5. **UserRegisteredEventListener mejorado**

- ✅ Usa `@Payload UserRegisteredEvent` en lugar de `String message`
- ✅ Headers de Kafka para debugging (topic, partition, offset)
- ✅ Logs estructurados con emojis para fácil identificación
- ✅ Validación de datos del evento
- ✅ Manejo de errores mejorado con re-lanzamiento para reintentos
- ✅ Condicional `@ConditionalOnProperty` para habilitar/deshabilitar

```java
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
    log.info("📥 Received UserRegistered event: userId={}, topic={}, partition={}, offset={}", 
        event.getUserId(), topic, partition, offset);
    // ...
}
```

### 6. **Test de Conectividad Kafka**

Nuevo componente `KafkaConnectionTest` que:
- ✅ Se ejecuta automáticamente al iniciar la aplicación
- ✅ Envía un mensaje de prueba al topic `test-topic`
- ✅ Logs claros de éxito o fallo
- ✅ Solo se ejecuta si Kafka está habilitado

---

## 🔐 Variables de Entorno Requeridas

Crea un archivo `.env` con estas variables (ver `.env.example`):

```bash
# Kafka Azure Event Hubs
KAFKA_BOOTSTRAP_SERVERS=your-namespace.servicebus.windows.net:9093
KAFKA_CONNECTION_STRING=Endpoint=sb://your-namespace.servicebus.windows.net/;SharedAccessKeyName=YourPolicy;SharedAccessKey=YourKey

# Topics
KAFKA_CONSUMER_GROUP_ID=profile-service-group
KAFKA_TOPIC_USER_REGISTERED=iam.user.registered
KAFKA_TOPIC_CHALLENGE_COMPLETED=challenge.completed
KAFKA_ENABLED=true
```

---

## 🧪 Verificación de la Configuración

### 1. Logs al Iniciar

Si todo está configurado correctamente, verás:

```log
✅ Kafka Consumer configured with SASL_SSL - Bootstrap: xxx.servicebus.windows.net:9093, GroupId: profile-service-group
✅ Kafka Listener Container Factory configured with error handling
🧪 Testing Kafka connection...
✅ Kafka connection test SUCCESS - Message sent to topic: test-topic, partition: 0, offset: 123
```

### 2. Al Recibir un Evento

```log
📥 Received UserRegistered event: userId=abc123, email=user@example.com, username=john_doe, topic=iam.user.registered, partition=0, offset=456
➕ Creating new profile for userId=abc123
✅ Successfully created profile for userId=abc123, username=USER123456789, profileId=1
```

### 3. Si hay Errores

```log
❌ Error processing Kafka record after retries: key=abc, value={...}, topic=iam.user.registered, partition=0, offset=789
```

---

## 🔍 Troubleshooting

### Error: "Cannot connect to Kafka"

**Síntomas:**
```
Failed to construct kafka consumer
Connection to node -1 could not be established
```

**Soluciones:**
1. Verifica que `KAFKA_BOOTSTRAP_SERVERS` tenga el formato correcto:
   ```
   your-namespace.servicebus.windows.net:9093
   ```
2. Verifica el `KAFKA_CONNECTION_STRING`
3. Revisa que el puerto sea `9093` (SASL_SSL)

---

### Error: "Authentication failed"

**Síntomas:**
```
Authentication failed: Invalid credentials
SASL authentication failed
```

**Soluciones:**
1. Verifica el connection string completo:
   ```bash
   Endpoint=sb://NAMESPACE.servicebus.windows.net/;SharedAccessKeyName=POLICY;SharedAccessKey=KEY
   ```
2. Asegúrate de que el policy tenga permisos de `Listen` y `Send`
3. Verifica que no haya espacios extra en el connection string

---

### Error: "Deserialization failed"

**Síntomas:**
```
Error deserializing key/value for partition
Trusted packages configuration error
```

**Soluciones:**
1. Verifica que el DTO `UserRegisteredEvent` coincida con el evento del IAM
2. Asegúrate de que `trusted.packages` esté configurado como `*`
3. Verifica el type mapping en `application.yml`

---

### Error: "Topic does not exist"

**Síntomas:**
```
Unknown topic or partition
Topic 'iam.user.registered' not found
```

**Soluciones:**
1. Verifica que el topic exista en Azure Event Hubs
2. Verifica que el nombre del topic sea exacto (case-sensitive)
3. Si estás en local, crea el topic en tu Kafka local primero

---

## 🔬 Habilitar Logs de Depuración

Si necesitas más información, añade esto a `application.yml`:

```yaml
logging:
  level:
    org.apache.kafka: DEBUG
    org.springframework.kafka: DEBUG
    com.levelup.journey.platform.microserviceprofiles.profiles.infrastructure.messaging: DEBUG
```

---

## 📊 Comparación con IAM Service

| Aspecto | IAM Service | Profile Service |
|---------|-------------|-----------------|
| **Dependencias** | ✅ kafka-clients | ✅ kafka-clients (AÑADIDO) |
| **Configuración SASL** | ✅ SASL_SSL | ✅ SASL_SSL (CORREGIDO) |
| **Topics en app.kafka** | ✅ Sí | ✅ Sí (MOVIDO) |
| **JsonDeserializer** | ✅ Configurado | ✅ Configurado (CORREGIDO) |
| **Error Handler** | ✅ Con reintentos | ✅ Con reintentos (AÑADIDO) |
| **DTO Event** | ✅ Tiene | ✅ Tiene (CREADO) |
| **Connection Test** | ✅ Tiene | ✅ Tiene (CREADO) |

Ahora **ambos servicios tienen la misma configuración** y deberían conectarse correctamente.

---

## ✅ Estado Final

- ✅ Compilación exitosa (121 archivos)
- ✅ Configuración SASL_SSL completa
- ✅ Error handling con reintentos
- ✅ Logs estructurados y claros
- ✅ Test de conectividad automático
- ✅ DTOs tipados
- ✅ Configuración idéntica a IAM service

---

**Versión**: 1.0  
**Última actualización**: 3 de Noviembre, 2025  
**Proyecto**: Microservice-Profiles
