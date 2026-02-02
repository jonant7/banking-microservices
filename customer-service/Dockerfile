FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY customer-service/gradlew .
COPY customer-service/gradle gradle
COPY customer-service/settings.gradle .
COPY customer-service/build.gradle .
COPY customer-service/src src
COPY contracts ../contracts

ENV CONTRACTS_PATH=/contracts

RUN chmod +x gradlew
RUN ./gradlew bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]