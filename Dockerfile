FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY target/*.jar user.services.jar
ENTRYPOINT ["java", "-jar", "/user.services.jar"]
EXPOSE 9002