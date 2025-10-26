package com.bookstore.tests;

import com.bookstore.clients.AuthorsApiClient;
import com.bookstore.models.Author;
import com.bookstore.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Comprehensive test suite for Authors API
 * Covers happy paths and edge cases
 */
@Slf4j
@Epic("Bookstore API")
@Feature("Authors Management")
public class AuthorsApiTest {

    private AuthorsApiClient authorsApiClient;

    @BeforeClass
    public void setUp() {
        authorsApiClient = new AuthorsApiClient();
        log.info("Authors API Client initialized with base URL: {}", authorsApiClient.getBaseUrl());
    }

    // ==================== HAPPY PATH TESTS ====================

    @Test(priority = 1, description = "Verify that all authors can be retrieved successfully")
    @Story("Get All Authors")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify GET /api/v1/Authors returns list of authors with 200 status code")
    public void testGetAllAuthors_Success() {
        Response response = authorsApiClient.getAllAuthors();

        response.then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8; v=1.0")
                .body("$", not(empty()));

        log.info("Successfully retrieved all authors. Count: {}, Status Code: {}",
                response.jsonPath().getList("$").size(), response.getStatusCode());
    }

    @Test(priority = 2, description = "Verify that a specific author can be retrieved by valid ID")
    @Story("Get Author By ID")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify GET /api/v1/Authors/{id} returns the correct author details")
    public void testGetAuthorById_ValidId_Success() {
        int authorId = 1;
        Response response = authorsApiClient.getAuthorById(authorId);

        response.then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8; v=1.0")
                .body("id", equalTo(authorId))
                .body("firstName", notNullValue())
                .body("lastName", notNullValue());

        Author author = response.as(Author.class);
        assertThat(author.getId()).isEqualTo(authorId);
        assertThat(author.getFirstName()).isNotEmpty();
        assertThat(author.getLastName()).isNotEmpty();

        log.info("Successfully retrieved author: {} {} (ID: {}), Status Code: {}",
                author.getFirstName(), author.getLastName(), author.getId(), response.getStatusCode());
    }

    @Test(priority = 3, description = "Verify that a new author can be created successfully")
    @Story("Create Author")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify POST /api/v1/Authors creates a new author and returns 200 status")
    public void testCreateAuthor_ValidData_Success() {
        Author newAuthor = TestDataGenerator.generateRandomAuthor();

        Response response = authorsApiClient.createAuthor(newAuthor);

        response.then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8; v=1.0")
                .body("firstName", equalTo(newAuthor.getFirstName()))
                .body("lastName", equalTo(newAuthor.getLastName()))
                .body("idBook", equalTo(newAuthor.getIdBook()));

        Author createdAuthor = response.as(Author.class);
        assertThat(createdAuthor.getFirstName()).isEqualTo(newAuthor.getFirstName());
        assertThat(createdAuthor.getLastName()).isEqualTo(newAuthor.getLastName());

        log.info("Successfully created author: {} {} (ID: {}), Status Code: {}",
                createdAuthor.getFirstName(), createdAuthor.getLastName(), createdAuthor.getId(), response.getStatusCode());
    }

    @Test(priority = 4, description = "Verify that an existing author can be updated successfully")
    @Story("Update Author")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify PUT /api/v1/Authors/{id} updates the author details")
    public void testUpdateAuthor_ValidData_Success() {
        // First, create a new author to ensure test isolation
        Author newAuthor = TestDataGenerator.generateRandomAuthor();
        Response createResponse = authorsApiClient.createAuthor(newAuthor);
        createResponse.then().statusCode(200);

        int createdAuthorId = createResponse.jsonPath().getInt("id");
        log.info("Created author for update test with ID: {}", createdAuthorId);

        // Update the created author with new data
        Author updatedAuthor = TestDataGenerator.generateAuthorWithId(createdAuthorId);
        updatedAuthor.setFirstName("UpdatedFirstName-" + System.currentTimeMillis());
        updatedAuthor.setLastName("UpdatedLastName");

        Response updateResponse = authorsApiClient.updateAuthor(createdAuthorId, updatedAuthor);

        updateResponse.then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8; v=1.0")
                .body("id", equalTo(createdAuthorId))
                .body("firstName", equalTo(updatedAuthor.getFirstName()))
                .body("lastName", equalTo(updatedAuthor.getLastName()));

        Author returnedAuthor = updateResponse.as(Author.class);
        assertThat(returnedAuthor.getId()).isEqualTo(createdAuthorId);
        assertThat(returnedAuthor.getFirstName()).isEqualTo(updatedAuthor.getFirstName());

        log.info("Successfully updated author with ID: {}, Status Code: {}", createdAuthorId, updateResponse.getStatusCode());
    }

    @Test(priority = 5, description = "Verify that an existing author can be deleted successfully")
    @Story("Delete Author")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify DELETE /api/v1/Authors/{id} deletes the author")
    public void testDeleteAuthor_ValidId_Success() {
        // First, create a new author to ensure test isolation
        Author newAuthor = TestDataGenerator.generateRandomAuthor();
        Response createResponse = authorsApiClient.createAuthor(newAuthor);
        createResponse.then().statusCode(200);

        int createdAuthorId = createResponse.jsonPath().getInt("id");
        log.info("Created author for delete test with ID: {}", createdAuthorId);

        // Delete the created author
        Response deleteResponse = authorsApiClient.deleteAuthor(createdAuthorId);

        deleteResponse.then()
                .statusCode(200);

        log.info("Successfully deleted author with ID: {}, Status Code: {}", createdAuthorId, deleteResponse.getStatusCode());

        // Verify the author is actually deleted by trying to GET it
        Response verifyResponse = authorsApiClient.getAuthorById(createdAuthorId);
        verifyResponse.then()
                .statusCode(404);

        log.info("Verified author with ID: {} is deleted (GET returns 404)", createdAuthorId);
    }

    // ==================== EDGE CASE TESTS ====================

    @Test(priority = 6, description = "Verify handling of non-existent author ID")
    @Story("Get Author By ID")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify GET /api/v1/Authors/{id} with non-existent ID returns 404")
    public void testGetAuthorById_NonExistentId() {
        int nonExistentId = 999999;

        Response response = authorsApiClient.getAuthorById(nonExistentId);

        response.then()
                .statusCode(404);

        log.info("Correctly handled non-existent author ID: {}, Status Code: {}", nonExistentId, response.getStatusCode());
    }

    @Test(priority = 7, description = "Verify handling of invalid author ID (negative)")
    @Story("Get Author By ID")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify GET /api/v1/Authors/{id} with negative ID returns 404")
    public void testGetAuthorById_NegativeId() {
        int negativeId = -1;

        Response response = authorsApiClient.getAuthorById(negativeId);

        // FakeRestAPI returns 404 for negative IDs
        response.then()
                .statusCode(404);

        log.info("Tested with negative ID: {}, Status Code: {}", negativeId, response.getStatusCode());
    }

    @Test(priority = 8, description = "Verify handling of zero as author ID")
    @Story("Get Author By ID")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify GET /api/v1/Authors/{id} with ID = 0 returns 404")
    public void testGetAuthorById_ZeroId() {
        int zeroId = 0;

        Response response = authorsApiClient.getAuthorById(zeroId);

        // FakeRestAPI returns 404 for zero ID
        response.then()
                .statusCode(404);

        log.info("Tested with zero ID: {}, Status Code: {}", zeroId, response.getStatusCode());
    }

    @Test(priority = 9, description = "Verify creating author with empty first name")
    @Story("Create Author")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify POST /api/v1/Authors with empty first name should return 400")
    public void testCreateAuthor_EmptyFirstName() {
        Author authorWithEmptyFirstName = TestDataGenerator.generateRandomAuthor();
        authorWithEmptyFirstName.setFirstName("");

        Response response = authorsApiClient.createAuthor(authorWithEmptyFirstName);

        // Expected: 400 Bad Request (firstName is required, empty string should be rejected)
        // Actual: FakeRestAPI incorrectly returns 200 - this is a bug
        response.then()
                .statusCode(400);

        log.info("Tested creating author with empty first name. Expected: 400, Actual: {}", response.getStatusCode());
    }

    @Test(priority = 10, description = "Verify creating author with null last name")
    @Story("Create Author")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify POST /api/v1/Authors with null last name should return 400")
    public void testCreateAuthor_NullLastName() {
        Author authorWithNullLastName = TestDataGenerator.generateRandomAuthor();
        authorWithNullLastName.setLastName(null);

        Response response = authorsApiClient.createAuthor(authorWithNullLastName);

        // Expected: 400 Bad Request (lastName is required field)
        // Actual: FakeRestAPI incorrectly returns 200 - this is a bug
        response.then()
                .statusCode(400);

        log.info("Tested creating author with null last name. Expected: 400, Actual: {}", response.getStatusCode());
    }

    @Test(priority = 11, description = "Verify creating author with negative book ID")
    @Story("Create Author")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify POST /api/v1/Authors with negative book ID should return 400")
    public void testCreateAuthor_NegativeBookId() {
        Author authorWithNegativeBookId = TestDataGenerator.generateRandomAuthor();
        authorWithNegativeBookId.setIdBook(-100);

        Response response = authorsApiClient.createAuthor(authorWithNegativeBookId);

        // Expected: 400 Bad Request (negative book ID is invalid)
        // Actual: FakeRestAPI incorrectly returns 200 - this is a bug
        response.then()
                .statusCode(400);

        log.info("Tested creating author with negative book ID. Expected: 400, Actual: {}", response.getStatusCode());
    }

    @Test(priority = 12, description = "Verify creating author with very long first name")
    @Story("Create Author")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test boundary condition with very long first name (500 characters)")
    public void testCreateAuthor_VeryLongFirstName() {
        Author authorWithLongFirstName = TestDataGenerator.generateRandomAuthor();
        authorWithLongFirstName.setFirstName(TestDataGenerator.generateLongString(500));

        Response response = authorsApiClient.createAuthor(authorWithLongFirstName);

        // FakeRestAPI accepts very long first names
        response.then()
                .statusCode(200);

        log.info("Tested creating author with very long first name (500 chars), Status Code: {}", response.getStatusCode());
    }

    @Test(priority = 13, description = "Verify creating author with very long last name")
    @Story("Create Author")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test boundary condition with very long last name (500 characters)")
    public void testCreateAuthor_VeryLongLastName() {
        Author authorWithLongLastName = TestDataGenerator.generateRandomAuthor();
        authorWithLongLastName.setLastName(TestDataGenerator.generateLongString(500));

        Response response = authorsApiClient.createAuthor(authorWithLongLastName);

        // FakeRestAPI accepts very long last names
        response.then()
                .statusCode(200);

        log.info("Tested creating author with very long last name (500 chars), Status Code: {}", response.getStatusCode());
    }

    @Test(priority = 14, description = "Verify creating author with special characters in name")
    @Story("Create Author")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test creating author with special characters in first and last name")
    public void testCreateAuthor_SpecialCharactersInName() {
        Author authorWithSpecialChars = TestDataGenerator.generateRandomAuthor();
        authorWithSpecialChars.setFirstName("Test@#$%");
        authorWithSpecialChars.setLastName("Name!&*()");

        Response response = authorsApiClient.createAuthor(authorWithSpecialChars);

        // FakeRestAPI accepts special characters in names
        response.then()
                .statusCode(200);

        log.info("Tested creating author with special characters in name, Status Code: {}", response.getStatusCode());
    }

    @Test(priority = 15, description = "Verify updating non-existent author")
    @Story("Update Author")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify PUT /api/v1/Authors/{id} with non-existent ID should return 404")
    public void testUpdateAuthor_NonExistentId() {
        int nonExistentId = 999999;
        Author author = TestDataGenerator.generateAuthorWithId(nonExistentId);

        Response response = authorsApiClient.updateAuthor(nonExistentId, author);

        // Expected: 404 Not Found (can't update non-existent resource)
        // Actual: FakeRestAPI incorrectly returns 200 - this is a bug
        response.then()
                .statusCode(404);

        log.info("Tested updating non-existent author ID: {}. Expected: 404, Actual: {}", nonExistentId, response.getStatusCode());
    }

    @Test(priority = 16, description = "Verify updating author with mismatched ID")
    @Story("Update Author")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify PUT /api/v1/Authors/{id} when path ID doesn't match body ID should return 400")
    public void testUpdateAuthor_MismatchedId() {
        int pathId = 1;
        int bodyId = 999;
        Author author = TestDataGenerator.generateAuthorWithId(bodyId);

        Response response = authorsApiClient.updateAuthor(pathId, author);

        // Expected: 400 Bad Request (path ID and body ID must match)
        // Actual: FakeRestAPI incorrectly accepts mismatched IDs and returns 200 - this is a bug
        response.then()
                .statusCode(400);

        log.info("Tested updating author with mismatched IDs - Path: {}, Body: {}. Expected: 400, Actual: {}",
                pathId, bodyId, response.getStatusCode());
    }

    @Test(priority = 17, description = "Verify deleting non-existent author")
    @Story("Delete Author")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify DELETE /api/v1/Authors/{id} with non-existent ID should return 404")
    public void testDeleteAuthor_NonExistentId() {
        int nonExistentId = 9999999;

        Response response = authorsApiClient.deleteAuthor(nonExistentId);

        // Expected: 404 Not Found (can't delete non-existent resource)
        // Actual: FakeRestAPI incorrectly returns 200 - this is a bug
        response.then()
                .statusCode(404);

        log.info("Tested deleting non-existent author ID: {}. Expected: 404, Actual: {}", nonExistentId, response.getStatusCode());
    }

    @Test(priority = 18, description = "Verify deleting author with negative ID")
    @Story("Delete Author")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify DELETE /api/v1/Authors/{id} with negative ID should return 404")
    public void testDeleteAuthor_NegativeId() {
        int negativeId = -1;

        Response response = authorsApiClient.deleteAuthor(negativeId);

        // Expected: 404 Not Found or 400 Bad Request (invalid ID)
        // Actual: FakeRestAPI incorrectly returns 200 - this is a bug
        response.then()
                .statusCode(404);

        log.info("Tested deleting author with negative ID: {}. Expected: 404, Actual: {}", negativeId, response.getStatusCode());
    }

    @Test(priority = 19, description = "Verify response time for getting all authors")
    @Story("Performance Test")
    @Severity(SeverityLevel.MINOR)
    @Description("Test to verify GET /api/v1/Authors responds within acceptable time")
    public void testGetAllAuthors_ResponseTime() {
        Response response = authorsApiClient.getAllAuthors();

        response.then()
                .statusCode(200)
                .time(lessThan(5000L)); // 5 seconds

        log.info("Response time for getting all authors: {} ms", response.time());
    }

    @Test(priority = 20, description = "Verify creating author with null first name")
    @Story("Create Author")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify POST /api/v1/Authors with null first name (field omitted from JSON) should return 400")
    public void testCreateAuthor_NullFirstName() {
        Author authorWithNullFirstName = TestDataGenerator.generateRandomAuthor();
        authorWithNullFirstName.setFirstName(null);

        Response response = authorsApiClient.createAuthor(authorWithNullFirstName);

        // Expected: 400 Bad Request (firstName is a required field)
        // Actual: When firstName is null, Jackson omits the field from JSON,
        //         and FakeRestAPI incorrectly accepts missing field and returns 200 - this is a bug
        response.then()
                .statusCode(400);

        log.info("Tested creating author with null/missing firstName field. Expected: 400, Actual: {}", response.getStatusCode());
    }

    @Test(priority = 21, description = "Verify creating author with empty string last name")
    @Story("Create Author")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify POST /api/v1/Authors with empty string last name should return 400")
    public void testCreateAuthor_EmptyStringLastName() {
        Author authorWithEmptyLastName = TestDataGenerator.generateRandomAuthor();
        authorWithEmptyLastName.setLastName("");

        Response response = authorsApiClient.createAuthor(authorWithEmptyLastName);

        // Expected: 400 Bad Request (lastName is a required field, empty string should be rejected)
        // Actual: FakeRestAPI incorrectly returns 200 - this is a bug
        response.then()
                .statusCode(400);

        log.info("Tested creating author with empty string lastName. Expected: 400, Actual: {}", response.getStatusCode());
    }

    @Test(priority = 22, description = "Verify creating author with zero book ID")
    @Story("Create Author")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify POST /api/v1/Authors with zero idBook should return 400")
    public void testCreateAuthor_ZeroBookId() {
        Author authorWithZeroBookId = TestDataGenerator.generateRandomAuthor();
        authorWithZeroBookId.setIdBook(0);

        Response response = authorsApiClient.createAuthor(authorWithZeroBookId);

        // Expected: 400 Bad Request (zero is an invalid book ID, similar to negative)
        // Actual: FakeRestAPI incorrectly returns 200 - this is a bug
        response.then()
                .statusCode(400);

        log.info("Tested creating author with zero idBook. Expected: 400, Actual: {}", response.getStatusCode());
    }

    @Test(priority = 23, description = "Verify creating author with duplicate ID")
    @Story("Create Author")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify POST /api/v1/Authors with duplicate ID should return 409 or 400")
    public void testCreateAuthor_DuplicateId() {
        // Create first author
        Author firstAuthor = TestDataGenerator.generateRandomAuthor();
        int duplicateId = firstAuthor.getId();

        Response firstResponse = authorsApiClient.createAuthor(firstAuthor);
        firstResponse.then().statusCode(200);

        log.info("Created first author with ID: {}", duplicateId);

        // Try to create second author with same ID
        Author secondAuthor = TestDataGenerator.generateAuthorWithId(duplicateId);
        secondAuthor.setFirstName("DifferentFirstName-" + System.currentTimeMillis());
        secondAuthor.setLastName("DifferentLastName");

        Response secondResponse = authorsApiClient.createAuthor(secondAuthor);

        // Expected: 409 Conflict or 400 Bad Request (duplicate ID should be rejected)
        // Actual: FakeRestAPI incorrectly returns 200 - this is a bug
        secondResponse.then()
                .statusCode(anyOf(is(400), is(409)));

        log.info("Attempted to create author with duplicate ID: {}. Expected: 400/409, Actual: {}",
                duplicateId, secondResponse.getStatusCode());
    }

    @Test(priority = 24, description = "Verify author data structure integrity")
    @Story("Data Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify author object has all required fields with correct data types")
    public void testGetAuthorById_DataStructureValidation() {
        int authorId = 1;
        Response response = authorsApiClient.getAuthorById(authorId);

        Author author = response.as(Author.class);

        // Validate all fields exist and have correct data types
        assertThat(author.getId()).isNotNull().isInstanceOf(Integer.class);
        assertThat(author.getFirstName()).isNotNull().isInstanceOf(String.class);
        assertThat(author.getLastName()).isNotNull().isInstanceOf(String.class);
        assertThat(author.getIdBook()).isNotNull().isInstanceOf(Integer.class);

        log.info("Author data structure validated successfully for ID: {} - All 4 fields present with correct types, Status Code: {}",
                authorId, response.getStatusCode());
    }

    @Test(priority = 25, description = "Verify creating author with maximum integer values")
    @Story("Create Author")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test boundary condition with maximum integer value for IDs")
    public void testCreateAuthor_MaxIntegerValues() {
        Author authorWithMaxValues = TestDataGenerator.generateRandomAuthor();
        authorWithMaxValues.setId(Integer.MAX_VALUE);
        authorWithMaxValues.setIdBook(Integer.MAX_VALUE);

        Response response = authorsApiClient.createAuthor(authorWithMaxValues);

        // FakeRestAPI accepts maximum integer values
        response.then()
                .statusCode(200);

        log.info("Tested creating author with maximum integer values, Status Code: {}", response.getStatusCode());
    }
}