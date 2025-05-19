# 1. Build aşaması
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
WORKDIR /app/api-gateway
RUN mvn -f /app/api-gateway/pom.xml clean package -DskipTests

# 2. Run aşaması
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/api-gateway/target/*.jar application.jar
RUN apt-get update && apt-get install -y curl --no-install-recommends && rm -rf /var/lib/apt/lists/*
ENTRYPOINT ["java", "-jar", "/app/application.jar"] 