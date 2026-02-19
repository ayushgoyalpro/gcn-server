# Use official OpenJDK 21 image
FROM eclipse-temurin:21

# Set working directory to /app
WORKDIR /app

# Copy only the Maven wrapper and pom.xml for dependency caching
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose the port (commonly 8080)
EXPOSE 8080

# Run the built JAR with the Spring 'cloud' profile
CMD ["java", "-jar", "target/google-calendar-notifications-0.0.1-SNAPSHOT.jar"]