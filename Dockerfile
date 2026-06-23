FROM eclipse-temurin:17-jdk-alpine

LABEL org.opencontainers.image.title="campanas-service"
LABEL org.opencontainers.image.description="Microservicio de campanas comerciales - KUBOCODE"
LABEL org.opencontainers.image.version="latest"

ENV TZ=America/Guayaquil
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app

ARG JAR_FILE=target/campanas-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -Dfile.encoding=UTF-8 -Duser.timezone=America/Guayaquil"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_TOOL_OPTIONS -Dspring.profiles.active=prod -jar app.jar"]
