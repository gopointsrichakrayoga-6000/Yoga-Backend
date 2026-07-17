# Stage 1: Build the Spring Boot application using Maven and Alpine JDK
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw && ./mvnw dependency:go-offline -B || true

# Copy source code and package executable JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests=true

# Stage 2: Minimal runtime image running as non-root user for high security
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# Create a secure non-root system user and group
RUN addgroup -S ashram && adduser -S ashram -G ashram

# Create persistent storage directories for H2 database and media uploads with proper permissions
RUN mkdir -p /app/data /app/uploads && chown -R ashram:ashram /app

# Copy the compiled executable JAR from builder stage
COPY --from=builder --chown=ashram:ashram /app/target/ashram-media-platform-backend-*.jar /app/app.jar

# Switch to non-root user
USER ashram

# Expose server port
EXPOSE 8081

# Launch Spring Boot application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
