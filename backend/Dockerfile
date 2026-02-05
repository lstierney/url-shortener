# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -q -e -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/url-shortener-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# ---- Periodic Healthcheck ----
HEALTHCHECK --interval=10s --timeout=3s --retries=5 \
  CMD wget -qO- http://localhost:8080/urls || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
