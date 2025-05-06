# Use a standard OpenJDK 17 slim image
FROM openjdk:17-jdk-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set the working directory inside the container
WORKDIR /app

# Argument to specify the JAR file name (defined in pom.xml)
# Maven repackage goal typically creates target/<artifactId>-<version>.jar
ARG JAR_FILE=target/*.jar

# Copy the JAR file from the build context to the container
COPY ${JAR_FILE} application.jar

# Expose the port the application runs on (port will be defined in application.yml or overridden by Docker Compose)
# We don't strictly need EXPOSE here as Docker Compose handles port mapping,
# but it's good practice for documentation.
# EXPOSE 8761 # Example for Discovery Server

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/application.jar"] 