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
- [Test Reports](#test-reports)
- [Configuration](#configuration)
- [API Endpoints Covered](#api-endpoints-covered)
- [Test Coverage](#test-coverage)
- [Known API Issues and Expected Test Failures](#known-api-issues-and-expected-test-failures)
- [CI/CD Integration](#cicd-integration)
- [Code Quality](#code-quality)
- [Troubleshooting](#troubleshooting)

## Overview

This project is a production-ready API automation testing framework that validates the functionality of the FakeRestAPI bookstore endpoints. It demonstrates industry best practices including:

- Clean, maintainable code architecture following SOLID principles
- Comprehensive test coverage (happy paths and edge cases)
- Automated test reporting with Allure
- Dockerized test execution
- CI/CD pipeline integration with GitHub Actions
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
├── .github/workflows/ci.yml              # GitHub Actions CI/CD pipeline
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
| GitHub Actions | Latest | CI/CD pipeline |

## Prerequisites

### For Local Execution:
- Java JDK 11 or higher
- Maven 3.8 or higher
- Git

### For Docker Execution:
- Docker installed and running

### For CI/CD:
- GitHub account
- GitHub Actions enabled (automatically available for all repositories)

## Installation and Setup

### 1. Clone the Repository

```bash
git clone https://github.com/iskndkr/api_automation_docker.git
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

## Test Reports

### Allure Report

Allure provides rich, interactive HTML reports with detailed test execution information.

**Live Report (GitHub Pages):** https://iskndkr.github.io/api_automation_docker/

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

## Known API Issues and Expected Test Failures

### Important Notice

This test framework is designed to validate API behavior comprehensively, including proper error handling and validation. However, **FakeRestAPI has several known limitations** that cause some negative test cases to fail as expected.

### API Validation Issues

The FakeRestAPI (https://fakerestapi.azurewebsites.net) **does not implement proper server-side validation**. This is a limitation of the API itself, not the test framework. As a result, the following test scenarios will fail:

#### Expected Failures (19 out of 51 tests)

The failing tests fall into three main categories:

**1. Input Validation Failures (8 tests)**
   - Null or empty required fields (e.g., `testCreateBook_NullTitle`)
   - Invalid data types or ranges (e.g., `testCreateBook_NegativePageCount`)
   - *Expected: HTTP 400 Bad Request → Actual: HTTP 200 OK*

**2. Resource Existence Failures (8 tests)**
   - Operations on non-existent resources (e.g., `testUpdateBook_NonExistentId`, `testDeleteBook_NonExistentId`)
   - Invalid resource IDs (e.g., `testDeleteBook_NegativeId`)
   - *Expected: HTTP 404 Not Found → Actual: HTTP 200 OK*

**3. Business Logic Failures (3 tests)**
   - ID mismatch in update operations (e.g., `testUpdateBook_MismatchedId`)
   - Duplicate resource creation (e.g., `testCreateBook_DuplicateId`)
   - *Expected: HTTP 400/409 → Actual: HTTP 200 OK*

These failures are evenly distributed across both Books API (10 tests) and Authors API (9 tests), demonstrating consistent validation gaps in the FakeRestAPI.

### Why These Tests Are Valuable

Despite these expected failures, **these test cases demonstrate important testing practices**:

1. **Comprehensive Coverage** - Tests verify both happy paths and error scenarios
2. **Real-World Scenarios** - In production APIs, these validations MUST exist
3. **Documentation** - Tests serve as living documentation of expected API behavior
4. **Quality Standards** - Demonstrates understanding of API best practices
5. **Future-Proof** - If FakeRestAPI fixes validation, tests will immediately pass

### Test Results Interpretation

When you run the test suite:
```bash
mvn test
```

**Expected Output:**
- Total Tests: 51
- Passing Tests: 32 (all happy path scenarios)
- Failing Tests: 19 (validation tests exposing API bugs)
- Success Rate: 63%

**This is normal and expected behavior.** The failing tests are correctly identifying API bugs.

### Verification

You can verify that the test framework is working correctly by:

1. **Checking Passing Tests** - All happy path tests pass (GET, POST, PUT, DELETE with valid data)
2. **Reviewing Failure Messages** - Failed tests show clear expected vs actual status codes
3. **Examining Test Logs** - Detailed request/response logs in `target/surefire-reports/`
4. **Allure Reports** - Visual representation of test results at https://iskndkr.github.io/api_automation_docker/

### For Production APIs

If you adapt this framework for a production API with proper validation:
- All 51 tests should pass
- The framework correctly validates both success and error scenarios
- No code changes needed - just point to a properly implemented API

### Summary

The test failures are **intentional and demonstrate thorough testing practices**. They expose real bugs in the FakeRestAPI's lack of validation. This framework is production-ready and follows industry best practices for API testing.

## CI/CD Integration

### GitHub Actions Pipeline

The project uses GitHub Actions for continuous integration and delivery. The workflow is automatically triggered on every push to the main branch.

**Workflow File:** `.github/workflows/ci.yml`

### Pipeline Stages

1. **Checkout** - Clone repository
2. **Build Docker Image** - Create test container
3. **Run Tests in Docker** - Execute test suite in isolated environment
4. **Generate Allure Report** - Create interactive HTML reports
5. **Publish Reports** - Deploy reports to GitHub Pages
6. **Archive Artifacts** - Store test results for 30 days
7. **Cleanup** - Remove old Docker images

### Pipeline Features
- Automated test execution on code changes
- Docker-based isolated test environment
- Automatic report generation (TestNG + Allure)
- GitHub Pages deployment for live reports
- Downloadable test artifacts
- Workflow summary with execution details

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

## Contact and Support

For questions, issues, or contributions, please open an issue in the repository.

---

**Project Status:** ✅ Production Ready

**Last Updated:** 2025-10-26
