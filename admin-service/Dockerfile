FROM gradle:8-jdk24 AS build
WORKDIR /
COPY . .
RUN gradle :admin-service:clean :admin-service:build

FROM openjdk:24-jdk
COPY --from=build /admin-service/build/libs/admin-service-1.0.0.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
