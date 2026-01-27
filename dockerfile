FROM openjdk:21-ea-1-jdk-slim
VOLUME /tmp
COPY target/tutormentor-0.1.jar app.jar
COPY keystore.p12 keystore.p12
ENTRYPOINT ["java", "-jar", "/app.jar"]
