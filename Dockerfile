FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY build.gradle /app
COPY settings.gradle /app
RUN gradle dependencies --no-daemon

COPY src /app/src
RUN gradle build -x test --no-daemon

FROM openjdk:21-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 9062
CMD ["java", "-jar", "app.jar"]
