FROM openjdk:24-jdk
ARG JAR_FILE=build/libs/EmployeeService-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-Dspring.profiles.active=dev", "-jar", "/app.jar"]
