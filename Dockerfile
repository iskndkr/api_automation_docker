# Use Maven with JDK 11 as base image
FROM maven:3.8.7-eclipse-temurin-11

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies (layer caching optimization)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the entire project
COPY . .

# Set environment variables with default values (can be overridden at runtime)
ENV BASE_URL=https://fakerestapi.azurewebsites.net
ENV API_VERSION=/api/v1
ENV LOG_LEVEL=INFO

# Create logs directory
RUN mkdir -p logs

# Expose port for Allure report server (optional)
EXPOSE 8080

# Default command: Run tests and generate Allure report
CMD ["sh", "-c", "mvn clean test -Dbase.url=${BASE_URL} -Dapi.version=${API_VERSION} -Dlog.level=${LOG_LEVEL} && mvn allure:report"]

# Alternative commands that can be used:
# To only run tests: docker run <image> mvn clean test
# To generate report: docker run <image> mvn allure:report
# To serve report: docker run -p 8080:8080 <image> mvn allure:serve
