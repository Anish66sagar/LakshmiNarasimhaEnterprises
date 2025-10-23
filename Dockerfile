# Use OpenJDK 17 slim as base image
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy Gradle wrapper and build files first (for caching)
COPY backend/gradlew .
COPY backend/gradle gradle
COPY backend/build.gradle .
COPY backend/settings.gradle .

# Copy source code
COPY backend/src src

# Build the project (skip tests to speed up build)
RUN ./gradlew build -x test

# Copy the built jar to a standard app.jar
COPY backend/build/libs/*.jar app.jar

# Expose port 8080 for the application
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
