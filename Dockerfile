# Stage 1: Build using Eclipse Temurin JDK 22 and manual Maven install
FROM eclipse-temurin:22 as build

# Install Maven manually
RUN apt-get update && apt-get install -y curl unzip && \
    curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.4/binaries/apache-maven-3.9.4-bin.zip -o maven.zip && \
    unzip maven.zip -d /opt && \
    ln -s /opt/apache-maven-3.9.4 /opt/maven && \
    ln -s /opt/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME=/opt/maven

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:22
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]



