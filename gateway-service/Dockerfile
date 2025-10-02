FROM gradle:8-jdk24 AS build
WORKDIR /
COPY . .
RUN gradle :gateway-service:clean :gateway-service:build

FROM openjdk:24-jdk
COPY --from=build /gateway-service/build/libs/gateway-service-1.0.0.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
