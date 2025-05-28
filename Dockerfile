# Build stage
FROM eclipse-temurin:17-jdk as builder
WORKDIR /workspace
COPY . .
RUN ./gradlew build -x test

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar
COPY --from=builder /workspace/src/main/resources/ ./resources/

# Configurações de saúde e segurança
RUN mkdir -p /app/logs
EXPOSE 8080
USER 1001

ENTRYPOINT ["java", "-jar", "app.jar"]