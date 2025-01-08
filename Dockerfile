# Base image
FROM openjdk:17-slim

# Set working directory
WORKDIR /app

# Copy application jar to container
COPY build/libs/somsomparty-0.0.1-SNAPSHOT.jar /app/app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]