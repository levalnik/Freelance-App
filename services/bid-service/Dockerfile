FROM openjdk:23

WORKDIR /app

COPY target/bid-service-1.0-SNAPSHOT.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]