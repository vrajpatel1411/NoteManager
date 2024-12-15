
FROM maven:3.8.5-openjdk-17 as builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ ./src/
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine as prod
RUN mkdir /app


ENV SERVER_PORT=8080


COPY --from=builder /app/target/*.jar /app/app.jar

WORKDIR /app
EXPOSE 6060

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
