FROM gradle:8-jdk24 AS build
WORKDIR /
COPY . .
RUN gradle :employee-service:clean :employee-service:build

FROM openjdk:24-jdk
COPY --from=build /employee-service/build/libs/employee-service-1.0.0.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
