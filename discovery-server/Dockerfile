# 1. Build aşaması
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
WORKDIR /app/discovery-server
RUN mvn -f /app/discovery-server/pom.xml clean package -DskipTests

# 2. Run aşaması
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/discovery-server/target/*.jar application.jar
RUN apt-get update && apt-get install -y curl --no-install-recommends && rm -rf /var/lib/apt/lists/*

# Expose the port the application runs on (port will be defined in application.yml or overridden by Docker Compose)
# We don't strictly need EXPOSE here as Docker Compose handles port mapping,
# but it's good practice for documentation.
# EXPOSE 8761 # Example for Discovery Server

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/application.jar"] 