# Stage 1: Build the application using Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copy the parent pom.xml first to leverage Docker layer caching
COPY ../pom.xml ./
RUN mvn dependency:go-offline -f pom.xml -B

# Copy the service-specific pom.xml
COPY pom.xml .

# Copy the source code
COPY src ./src

# Build the application JAR
RUN mvn package -DskipTests

# Stage 2: Create the final lightweight image
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/campaign-event-service*.jar app.jar

# Expose the application port (should match application.yml)
EXPOSE 8086

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 