# ── Stage 1: Build ─────────────────────────
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy full project from build context
COPY . .

# Build only gateway module
RUN mvn -pl api-gateway -am clean package -DskipTests

# ── Stage 2: Runtime ───────────────────────
FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache wget

WORKDIR /app

# Copy built jar
COPY --from=builder /build/api-gateway/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]