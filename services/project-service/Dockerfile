FROM openjdk:23

WORKDIR /app

COPY target/project-service-1.0-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]