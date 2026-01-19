FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY account-service/gradlew .
COPY account-service/gradle gradle
COPY account-service/settings.gradle .
COPY account-service/build.gradle .
COPY account-service/src src
COPY contracts ../contracts

ENV CONTRACTS_PATH=/contracts

RUN chmod +x gradlew
RUN ./gradlew bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]