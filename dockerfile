FROM openjdk:21-jdk-slim
VOLUME /tmp
COPY target/tutormentor-0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

