# Start with a base image containing Java runtime
FROM eclipse-temurin:17-jdk-alpine

# Add Maintainer Info
LABEL maintainer="Your Name"

# JAR file from the target directory
ARG JAR_FILE=target/*.jar

# Add the jar to the image
COPY ${JAR_FILE} app.jar

# Expose the port that Spring Boot listens on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]
