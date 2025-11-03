# ✅ Kafka SASL_SSL - Checklist de Verificación

## 📋 Lista de Verificación Pre-Deploy

### 1. ✅ Dependencias
- [x] `spring-kafka` en pom.xml
- [x] `kafka-clients` en pom.xml (CRÍTICO para SASL_SSL)
- [x] `jackson-datatype-jsr310` para Instant/LocalDateTime

### 2. ✅ Variables de Entorno (.env)

```bash
# Verificar que existan y tengan valores correctos:
echo $KAFKA_BOOTSTRAP_SERVERS
echo $KAFKA_CONNECTION_STRING
echo $KAFKA_CONSUMER_GROUP_ID
echo $KAFKA_TOPIC_USER_REGISTERED
echo $KAFKA_ENABLED
```

**Formato esperado:**
- `KAFKA_BOOTSTRAP_SERVERS`: `namespace.servicebus.windows.net:9093`
- `KAFKA_CONNECTION_STRING`: `Endpoint=sb://...;SharedAccessKeyName=...;SharedAccessKey=...`
- Puerto debe ser `9093` (no 9092)

### 3. ✅ Archivos Creados/Modificados

```
✅ pom.xml (añadida dependencia kafka-clients)
✅ application.yml (movidos topics a app.kafka.topics)
✅ KafkaConsumerConfig.java (configuración SASL_SSL completa)
✅ UserRegisteredEvent.java (DTO creado)
✅ UserRegisteredEventListener.java (actualizado con @Payload)
✅ KafkaConnectionTest.java (test automático creado)
✅ .env.example (template creado)
✅ docs/KAFKA_CONFIGURATION.md (documentación completa)
```

### 4. ✅ Compilación

```bash
# Debe compilar sin errores
./mvnw clean compile

# Resultado esperado:
# [INFO] BUILD SUCCESS
# [INFO] Compiling 121 source files
```

### 5. ✅ Logs Esperados al Iniciar

Al iniciar la aplicación, debes ver:

```log
2025-11-03 09:30:00 INFO  - ✅ Kafka Consumer configured with SASL_SSL - Bootstrap: xxx.servicebus.windows.net:9093, GroupId: profile-service-group
2025-11-03 09:30:01 INFO  - ✅ Kafka Listener Container Factory configured with error handling
2025-11-03 09:30:02 INFO  - 🧪 Testing Kafka connection...
2025-11-03 09:30:03 INFO  - ✅ Kafka connection test SUCCESS - Message sent to topic: test-topic, partition: 0, offset: 123
```

### 6. ✅ Al Recibir Evento

Cuando el IAM service publique un evento, debes ver:

```log
2025-11-03 09:31:00 INFO  - 📥 Received UserRegistered event: userId=abc123, email=user@example.com, username=john_doe, topic=iam.user.registered, partition=0, offset=456
2025-11-03 09:31:00 INFO  - ➕ Creating new profile for userId=abc123
2025-11-03 09:31:00 INFO  - ✅ Successfully created profile for userId=abc123, username=USER123456789, profileId=1
```

---

## 🔍 Diagnóstico Rápido

### ¿La aplicación NO inicia?

```bash
# 1. Verifica las variables de entorno
cat .env | grep KAFKA

# 2. Verifica la compilación
./mvnw clean compile

# 3. Revisa los logs de inicio
./mvnw spring-boot:run
```

### ¿NO se conecta a Kafka?

```bash
# 1. Verifica la conectividad
ping your-namespace.servicebus.windows.net

# 2. Verifica el puerto (debe ser 9093)
telnet your-namespace.servicebus.windows.net 9093

# 3. Verifica el connection string
# No debe tener espacios ni saltos de línea
echo $KAFKA_CONNECTION_STRING
```

### ¿Se conecta pero NO recibe eventos?

```bash
# 1. Verifica que el topic exista
# En Azure Portal -> Event Hubs -> Tu namespace -> Event Hubs
# Debe existir: iam.user.registered

# 2. Verifica el consumer group
# En Azure Portal -> Event Hubs -> iam.user.registered -> Consumer groups
# Debe existir: profile-service-group

# 3. Verifica que IAM esté publicando
# Revisa los logs del IAM service
```

### ¿Recibe eventos pero falla al procesar?

```bash
# Habilita logs de depuración en application.yml:
logging:
  level:
    org.springframework.kafka: DEBUG
    com.levelup.journey.platform.microserviceprofiles: DEBUG

# Busca en los logs:
❌ Error processing UserRegistered event for userId=...
```

---

## 🧪 Testing Manual

### 1. Test de Producer (IAM Service)

Publica un evento desde IAM:

```java
POST /api/v1/users/register
{
  "email": "test@example.com",
  "password": "Test1234!",
  "firstName": "Test",
  "lastName": "User"
}
```

### 2. Verifica los Logs del Profile Service

Deberías ver:

```log
📥 Received UserRegistered event: userId=xxx, ...
➕ Creating new profile for userId=xxx
✅ Successfully created profile for userId=xxx
```

### 3. Verifica en la Base de Datos

```sql
SELECT * FROM profiles WHERE user_id = 'xxx';
```

---

## ❌ Errores Comunes y Soluciones

### Error 1: "Authentication failed"

```log
❌ SASL authentication failed: Invalid credentials
```

**Solución:**
```bash
# Verifica el connection string COMPLETO
echo $KAFKA_CONNECTION_STRING

# Debe ser:
Endpoint=sb://NAMESPACE.servicebus.windows.net/;SharedAccessKeyName=POLICY;SharedAccessKey=KEY

# NO debe tener:
- Espacios en blanco
- Saltos de línea
- Comillas dentro del string
```

### Error 2: "Connection refused"

```log
❌ Connection to node -1 could not be established
```

**Solución:**
```bash
# Verifica el puerto (debe ser 9093, NO 9092)
echo $KAFKA_BOOTSTRAP_SERVERS

# Debe ser:
your-namespace.servicebus.windows.net:9093
```

### Error 3: "Deserialization failed"

```log
❌ Error deserializing key/value for partition
```

**Solución:**
1. Verifica que `UserRegisteredEvent` tenga los mismos campos que el IAM
2. Verifica que `trusted.packages` esté en `*`
3. Verifica el type mapping en application.yml

### Error 4: "Topic does not exist"

```log
❌ Unknown topic or partition: iam.user.registered
```

**Solución:**
1. Crea el topic en Azure Event Hubs
2. Verifica el nombre exacto (case-sensitive)
3. Espera unos segundos a que se propague

---

## 🎯 Quick Start

```bash
# 1. Clonar/Actualizar el proyecto
git pull origin develop

# 2. Crear .env desde el template
cp .env.example .env
# Editar .env con tus valores reales

# 3. Compilar
./mvnw clean compile

# 4. Ejecutar
./mvnw spring-boot:run

# 5. Verificar logs
# Buscar: ✅ Kafka connection test SUCCESS

# 6. Publicar un evento desde IAM
# Registrar un nuevo usuario

# 7. Verificar que se creó el perfil
# Buscar en logs: ✅ Successfully created profile
```

---

## 📞 Support

Si después de seguir todos los pasos el problema persiste:

1. **Recopila información:**
   ```bash
   # Variables de entorno (sin valores sensibles)
   env | grep KAFKA
   
   # Logs de la aplicación
   tail -f logs/spring.log
   
   # Versiones
   java -version
   mvn -version
   ```

2. **Verifica el IAM service:**
   - ¿Está publicando eventos correctamente?
   - ¿Tiene la misma configuración de Kafka?

3. **Comparte el stacktrace completo** del error específico

---

**✅ Estado:** Configuración completa y lista para deployment  
**📅 Fecha:** 3 de Noviembre, 2025  
**🚀 Versión:** 1.0.0
