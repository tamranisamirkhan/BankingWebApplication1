# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Package the app (Skip tests for faster build)
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk-alpine

# Copy the jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 (default in Spring Boot)
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
