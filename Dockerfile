# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /build/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8081

# Run the application
# Render sets the PORT environment variable, so we need to handle both cases
CMD ["java", "-jar", "app.jar", "--server.port=${PORT:=8081}"]
