FROM openjdk:23

WORKDIR /app

COPY target/user-service-1.0-SNAPSHOT.jar user-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "user-service.jar"]