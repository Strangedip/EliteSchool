# Build context: elite-backend/
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY api-gateway ./api-gateway
RUN mvn -q -f api-gateway/pom.xml package -Dmaven.test.skip=true

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /workspace/api-gateway/target/api-gateway.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xms64m -Xmx256m -XX:+UseG1GC -XX:+UseCompactObjectHeaders"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
