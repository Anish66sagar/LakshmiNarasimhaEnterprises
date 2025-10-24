# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copy project files
COPY . .

# Fix Gradle permissions
RUN chmod +x ./gradlew

# Disable Gradle auto-download for toolchains
RUN echo "org.gradle.java.installations.auto-download=false" >> gradle.properties

# Build project (skip tests for faster build)
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/milk-delivery.jar ./milk-delivery.jar

# Expose port for Spring Boot
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","milk-delivery.jar"]
