FROM maven:3.9.4-eclipse-temurin-21

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Package the application and skip tests
RUN mvn clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/SpreadsheetReader-1.0-SNAPSHOT.jar"]
