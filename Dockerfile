#first stage: build jar file from source code
FROM maven:3.8.4-openjdk-11-slim AS builder
COPY ./pom.xml pom.xml
COPY ./src src/
RUN mvn clean package -Dmaven.test.skip

#second stage: minimal runtime
FROM adoptopenjdk/openjdk11:alpine
COPY --from=builder target/*.jar app.jar
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ENTRYPOINT ["java", "-Duser.timezone=UTC", "-jar", "/app.jar"]