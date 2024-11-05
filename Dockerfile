# Stage 1: Build the application with Maven using OpenJDK 17
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the Maven wrapper and pom.xml files for dependency resolution
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x ./mvnw

# Download dependencies without building the source code
RUN ./mvnw dependency:go-offline -B

# Copy the source code and build the application
COPY src src
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the final Docker image with OpenJDK 17
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 3000

# Set the entry point to run the JAR file
ENTRYPOINT ["java", "-jar", "/*.jar"]
