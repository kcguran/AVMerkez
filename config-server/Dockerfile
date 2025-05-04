# Use a standard OpenJDK 17 slim image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Argument to specify the JAR file name (defined in pom.xml)
ARG JAR_FILE=target/*.jar

# Copy the JAR file from the build context to the container
COPY ${JAR_FILE} application.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/application.jar"] 