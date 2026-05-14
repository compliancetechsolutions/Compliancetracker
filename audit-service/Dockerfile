# ── Stage 1: Build ─────────────────────────────────────────
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy entire project (needed for multi-module)
COPY ../pom.xml ./pom.xml
COPY . .

# Build only this module
RUN mvn clean package -DskipTests

# ── Stage 2: Runtime ───────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

# Install wget (for healthcheck)
RUN apk add --no-cache wget

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app
USER appuser

# Copy built JAR
COPY --from=builder /build/audit-service/target/*.jar app.jar
# Expose port (Spring Boot default overridden by service config)
EXPOSE 8080

# Healthcheck
HEALTHCHECK --interval=20s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java","-jar","/app/app.jar"]