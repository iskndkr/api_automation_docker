# Bookstore API Automation Framework

A comprehensive REST API test automation framework for FakeRestAPI's online bookstore, built with Java, RestAssured, TestNG, and Docker.

## Table of Contents

- [Overview](#overview)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Installation and Setup](#installation-and-setup)
- [Running Tests](#running-tests)
  - [Local Execution](#local-execution)
  - [Docker Execution](#docker-execution)
  - [Jenkins Pipeline](#jenkins-pipeline)
- [Test Reports](#test-reports)
- [Configuration](#configuration)
- [API Endpoints Covered](#api-endpoints-covered)
- [Test Coverage](#test-coverage)
- [CI/CD Integration](#cicd-integration)
- [Code Quality](#code-quality)
- [Troubleshooting](#troubleshooting)

## Overview

This project is a production-ready API automation testing framework that validates the functionality of the FakeRestAPI bookstore endpoints. It demonstrates industry best practices including:

- Clean, maintainable code architecture following SOLID principles
- Comprehensive test coverage (happy paths and edge cases)
- Automated test reporting with Allure
- Dockerized test execution
- CI/CD pipeline integration with Jenkins
- Configuration management with environment variable support

**Base API URL:** https://fakerestapi.azurewebsites.net/api/v1

## Project Structure

```
api_automation_docker/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── bookstore/
│   │               ├── clients/          # API client classes
│   │               │   ├── BaseApiClient.java
│   │               │   ├── BooksApiClient.java
│   │               │   └── AuthorsApiClient.java
│   │               ├── config/           # Configuration management
│   │               │   └── ConfigManager.java
│   │               ├── models/           # POJO models
│   │               │   ├── Book.java
│   │               │   └── Author.java
│   │               └── utils/            # Utility classes
│   │                   └── TestDataGenerator.java
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── bookstore/
│       │           ├── listeners/        # TestNG listeners
│       │           │   └── TestListener.java
│       │           └── tests/            # Test classes
│       │               ├── BooksApiTest.java
│       │               └── AuthorsApiTest.java
│       └── resources/
│           ├── config.properties         # Configuration properties
│           ├── logback.xml              # Logging configuration
│           └── allure.properties        # Allure configuration
├── Dockerfile                            # Docker container definition
├── .dockerignore                         # Docker ignore file
├── Jenkinsfile                           # Jenkins pipeline configuration
├── testng.xml                            # TestNG suite configuration
├── pom.xml                               # Maven dependencies
├── .gitignore                            # Git ignore file
└── README.md                             # This file
```

## Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 11 | Programming language |
| Maven | 3.8+ | Build and dependency management |
| RestAssured | 5.4.0 | REST API testing framework |
| TestNG | 7.8.0 | Test framework |
| Allure | 2.25.0 | Test reporting |
| Lombok | 1.18.30 | Reducing boilerplate code |
| Jackson | 2.16.1 | JSON serialization/deserialization |
| AssertJ | 3.25.1 | Fluent assertions |
| SLF4J/Logback | 2.0.9/1.4.14 | Logging |
| Docker | Latest | Containerization |
| Jenkins | Latest | CI/CD pipeline |

## Prerequisites

### For Local Execution:
- Java JDK 11 or higher
- Maven 3.8 or higher
- Git

### For Docker Execution:
- Docker installed and running
- Docker Compose (optional)

### For CI/CD:
- Jenkins server with Docker support
- Required Jenkins plugins:
  - Docker Pipeline
  - Allure Plugin
  - HTML Publisher Plugin

## Installation and Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd api_automation_docker
```

### 2. Verify Java and Maven Installation

```bash
java -version
mvn -version
```

### 3. Install Dependencies

```bash
mvn clean install -DskipTests
```

## Running Tests

### Local Execution

#### Run all tests:
```bash
mvn clean test
```

#### Run specific test class:
```bash
mvn test -Dtest=BooksApiTest
mvn test -Dtest=AuthorsApiTest
```

#### Run with custom base URL:
```bash
mvn test -Dbase.url=https://fakerestapi.azurewebsites.net
```

#### Run tests and generate Allure report:
```bash
mvn clean test
mvn allure:report
mvn allure:serve
```

### Docker Execution

#### Build Docker Image:
```bash
docker build -t bookstore-api-tests .
```

#### Run tests in Docker container:
```bash
docker run --rm bookstore-api-tests
```

#### Run with environment variables:
```bash
docker run --rm \
  -e BASE_URL=https://fakerestapi.azurewebsites.net \
  -e API_VERSION=/api/v1 \
  bookstore-api-tests
```

#### Run and extract test reports:
```bash
# Run container with name
docker run --name test-run bookstore-api-tests

# Copy reports from container
docker cp test-run:/app/target/allure-results ./allure-results
docker cp test-run:/app/target/surefire-reports ./surefire-reports

# Remove container
docker rm test-run
```

#### Interactive Docker execution (for debugging):
```bash
docker run -it --rm bookstore-api-tests bash
```

### Jenkins Pipeline

1. **Create a new Pipeline job in Jenkins**
2. **Configure the pipeline to use the Jenkinsfile from the repository**
3. **Set up required credentials and environment variables**
4. **Run the pipeline**

The pipeline will:
- Build the Docker image
- Run tests inside the container
- Generate and publish Allure reports
- Archive test results
- Send email notifications (if configured)

## Test Reports

### Allure Report

Allure provides rich, interactive HTML reports with detailed test execution information.

**Generate and view locally:**
```bash
mvn allure:report
mvn allure:serve
```

**Report features:**
- Test execution timeline
- Test categorization by features and stories
- Detailed step-by-step execution logs
- Request/response details for API calls
- Failure screenshots and logs
- Test execution trends

### TestNG Report

Standard TestNG HTML reports are generated in `target/surefire-reports/index.html`

### Console Logs

Detailed execution logs are available in:
- Console output
- `logs/test-execution.log` file

## Configuration

### Configuration Files

#### config.properties
```properties
base.url=https://fakerestapi.azurewebsites.net
api.version=/api/v1
books.endpoint=/Books
authors.endpoint=/Authors
request.timeout=30000
connection.timeout=10000
log.level=INFO
```

### Environment Variables

Configuration can be overridden using environment variables:

| Property | Environment Variable | Default Value |
|----------|---------------------|---------------|
| base.url | BASE_URL | https://fakerestapi.azurewebsites.net |
| api.version | API_VERSION | /api/v1 |
| log.level | LOG_LEVEL | INFO |

**Example:**
```bash
export BASE_URL=https://custom-api.example.com
mvn test
```

## API Endpoints Covered

### Books API
- `GET /api/v1/Books` - Retrieve all books
- `GET /api/v1/Books/{id}` - Retrieve specific book
- `POST /api/v1/Books` - Create new book
- `PUT /api/v1/Books/{id}` - Update existing book
- `DELETE /api/v1/Books/{id}` - Delete book

### Authors API
- `GET /api/v1/Authors` - Retrieve all authors
- `GET /api/v1/Authors/{id}` - Retrieve specific author
- `POST /api/v1/Authors` - Create new author
- `PUT /api/v1/Authors/{id}` - Update existing author
- `DELETE /api/v1/Authors/{id}` - Delete author

## Test Coverage

### Happy Path Tests
- Successful retrieval of all resources
- Successful retrieval by valid ID
- Successful creation with valid data
- Successful update with valid data
- Successful deletion with valid ID

### Edge Cases and Negative Tests
- Non-existent resource IDs (404 scenarios)
- Invalid IDs (negative numbers, zero)
- Empty/null required fields
- Boundary value testing (max integers, very long strings)
- Data validation (negative page counts, etc.)
- ID mismatch in update operations
- Performance testing (response time validation)
- Data structure integrity validation

**Total Test Cases:**
- Books API: 20 test cases
- Authors API: 22 test cases
- **Total: 42 comprehensive test cases**

## CI/CD Integration

### Jenkins Pipeline Stages

1. **Checkout** - Clone repository
2. **Build Docker Image** - Create test container
3. **Run Tests in Docker** - Execute test suite
4. **Generate Allure Report** - Create visual reports
5. **Publish Reports** - Make reports available
6. **Cleanup** - Remove old images and containers

### Pipeline Features
- Automated test execution on code changes
- Docker-based isolated test environment
- Parallel execution support
- Automatic report generation
- Email notifications on build status
- Artifact archiving
- Build history management

## Code Quality

### Design Principles Applied

1. **Single Responsibility Principle (SRP)**
   - Each class has one well-defined purpose
   - Separation of concerns (clients, models, tests, utils)

2. **Open/Closed Principle (OCP)**
   - BaseApiClient can be extended for new endpoints
   - Test data generation is extensible

3. **Liskov Substitution Principle (LSP)**
   - API clients extend BaseApiClient properly
   - Polymorphic behavior maintained

4. **Interface Segregation Principle (ISP)**
   - Focused interfaces for specific purposes
   - No fat interfaces

5. **Dependency Inversion Principle (DIP)**
   - Dependencies on abstractions (ConfigManager)
   - Loose coupling between components

### Code Quality Features
- Comprehensive logging with SLF4J
- Lombok for cleaner code
- Proper exception handling
- Clear naming conventions
- Extensive inline documentation
- Reusable utility classes
- Centralized configuration management

## Troubleshooting

### Common Issues

#### Tests fail with connection timeout
```bash
# Increase timeout in config.properties
request.timeout=60000
connection.timeout=20000
```

#### Docker build fails
```bash
# Clean Docker cache
docker system prune -a
docker build --no-cache -t bookstore-api-tests .
```

#### Allure report not generating
```bash
# Install Allure commandline
brew install allure  # macOS
# or download from https://docs.qameta.io/allure/

# Generate report manually
allure generate target/allure-results --clean -o allure-report
allure open allure-report
```

#### Maven dependency issues
```bash
# Force update dependencies
mvn clean install -U

# Clear local repository cache
rm -rf ~/.m2/repository
mvn clean install
```

#### Jenkins pipeline fails
- Ensure Docker is installed and running on Jenkins agent
- Verify Maven and JDK are configured in Global Tool Configuration
- Check required plugins are installed (Docker, Allure, HTML Publisher)
- Verify network connectivity to FakeRestAPI

## Contact and Support

For questions, issues, or contributions, please:
- Open an issue in the repository
- Contact the QA team
- Review the inline code documentation

---

**Project Status:** ✅ Production Ready

**Last Updated:** 2025-10-25

**Maintained by:** QA Automation Team
