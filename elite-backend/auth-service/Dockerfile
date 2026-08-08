# Build context must be elite-backend/ so common-utils is reachable.
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace

COPY common-utils ./common-utils
RUN mvn -q -f common-utils/pom.xml install -Dmaven.test.skip=true

COPY auth-service ./auth-service
RUN mvn -q -f auth-service/pom.xml package -Dmaven.test.skip=true

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /workspace/auth-service/target/auth-service.jar app.jar
EXPOSE 8081
ENV JAVA_OPTS="-Xms64m -Xmx320m -XX:+UseG1GC -XX:+UseCompactObjectHeaders"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

