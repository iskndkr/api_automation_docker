package com.bookstore.tests;

import com.bookstore.clients.BooksApiClient;
import com.bookstore.models.Book;
import com.bookstore.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Comprehensive test suite for Books API
 * Covers happy paths and edge cases
 */
@Slf4j
@Epic("Bookstore API")
@Feature("Books Management")
public class BooksApiTest {

    private BooksApiClient booksApiClient;

    @BeforeClass
    public void setUp() {
        booksApiClient = new BooksApiClient();
        log.info("Books API Client initialized with base URL: {}", booksApiClient.getBaseUrl());
    }

    // ==================== HAPPY PATH TESTS ====================

    @Test(priority = 1, description = "Verify that all books can be retrieved successfully")
    @Story("Get All Books")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify GET /api/v1/Books returns list of books with 200 status code")
    public void testGetAllBooks_Success() {
        Response response = booksApiClient.getAllBooks();

        response.then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8; v=1.0")
                .body("$", not(empty()));

        log.info("Successfully retrieved all books. Count: {}, Status Code: {}", response.jsonPath().getList("$").size(), response.getStatusCode());
    }

    @Test(priority = 2, description = "Verify that a specific book can be retrieved by valid ID")
    @Story("Get Book By ID")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify GET /api/v1/Books/{id} returns the correct book details")
    public void testGetBookById_ValidId_Success() {
        int bookId = 1;
        Response response = booksApiClient.getBookById(bookId);

        response.then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8; v=1.0")
                .body("id", equalTo(bookId))
                .body("title", notNullValue())
                .body("description", notNullValue());

        Book book = response.as(Book.class);
        assertThat(book.getId()).isEqualTo(bookId);
        assertThat(book.getTitle()).isNotEmpty();

        log.info("Successfully retrieved book: {} (ID: {}), Status Code: {}", book.getTitle(), book.getId(), response.getStatusCode());
    }

    @Test(priority = 3, description = "Verify that a new book can be created successfully")
    @Story("Create Book")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify POST /api/v1/Books creates a new book and returns 200 status")
    public void testCreateBook_ValidData_Success() {
        Book newBook = TestDataGenerator.generateRandomBook();

        Response response = booksApiClient.createBook(newBook);

        response.then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8; v=1.0")
                .body("title", equalTo(newBook.getTitle()))
                .body("description", equalTo(newBook.getDescription()))
                .body("pageCount", equalTo(newBook.getPageCount()));

        Book createdBook = response.as(Book.class);
        assertThat(createdBook.getTitle()).isEqualTo(newBook.getTitle());
        assertThat(createdBook.getDescription()).isEqualTo(newBook.getDescription());

        log.info("Successfully created book: {} (ID: {}), Status Code: {}", createdBook.getTitle(), createdBook.getId(), response.getStatusCode());
    }


    @Test(priority = 4, description = "Verify that an existing book can be updated")
    @Story("Update Book")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify PUT /api/v1/Books/{id} updates the book details")
    public void testUpdateBook_ValidData_Success() {
        // First, create a new book to ensure test isolation
        Book newBook = TestDataGenerator.generateRandomBook();
        Response createResponse = booksApiClient.createBook(newBook);
        createResponse.then().statusCode(200);

        int createdBookId = createResponse.jsonPath().getInt("id");
        log.info("Created book for update test with ID: {}", createdBookId);

        // update the created book with new data
        Book updatedBook = TestDataGenerator.generateBookWithId(createdBookId);
        updatedBook.setTitle("Updated Book Title - " + System.currentTimeMillis());
        updatedBook.setDescription("Updated book description");

        Response updateResponse = booksApiClient.updateBook(createdBookId, updatedBook);

        updateResponse.then()
                .statusCode(200)
                .contentType("application/json; charset=utf-8; v=1.0")
                .body("id", equalTo(createdBookId))
                .body("title", equalTo(updatedBook.getTitle()))
                .body("description", equalTo(updatedBook.getDescription()));

        Book returnedBook = updateResponse.as(Book.class);
        assertThat(returnedBook.getId()).isEqualTo(createdBookId);
        assertThat(returnedBook.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(returnedBook.getDescription()).isEqualTo(updatedBook.getDescription());

        log.info("Successfully updated book with ID: {}, Status Code: {}", createdBookId, updateResponse.getStatusCode());
    }

    @Test(priority = 5, description = "Verify that an existing book can be deleted successfully")
    @Story("Delete Book")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify DELETE /api/v1/Books/{id} deletes the book")
    public void testDeleteBook_ValidId_Success() {
        // First, create a new book to ensure test isolation
        Book newBook = TestDataGenerator.generateRandomBook();
        Response createResponse = booksApiClient.createBook(newBook);
        createResponse.then().statusCode(200);

        int createdBookId = createResponse.jsonPath().getInt("id");
        log.info("Created book for delete test with ID: {}", createdBookId);

        // delete the created book
        Response deleteResponse = booksApiClient.deleteBook(createdBookId);

        deleteResponse.then()
                .statusCode(200);

        log.info("Successfully deleted book with ID: {}, Status Code: {}", createdBookId, deleteResponse.getStatusCode());

        // Verify the book is actually deleted by trying to GET it
        Response verifyResponse = booksApiClient.getBookById(createdBookId);
        verifyResponse.then()
                .statusCode(404);

        log.info("Verified book with ID: {} is deleted (GET returns 404)", createdBookId);
    }

    // ==================== EDGE CASE TESTS ====================

    @Test(priority = 6, description = "Verify handling of non-existent book ID")
    @Story("Get Book By ID")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify GET /api/v1/Books/{id} with non-existent ID returns 404")
    public void testGetBookById_NonExistentId() {
        int nonExistentId = 999999;

        Response response = booksApiClient.getBookById(nonExistentId);

        response.then()
                .statusCode(404);

        log.info("Correctly handled non-existent book ID: {}, Status Code: {}", nonExistentId, response.getStatusCode());
    }

    @Test(priority = 7, description = "Verify handling of invalid book ID (negative)")
    @Story("Get Book By ID")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify GET /api/v1/Books/{id} with negative ID")
    public void testGetBookById_NegativeId() {
        int negativeId = -1;

        Response response = booksApiClient.getBookById(negativeId);

        // FakeRestAPI returns 404 for negative IDs
        response.then()
                .statusCode(404);

        log.info("Tested with negative ID: {}, Status Code: {}", negativeId, response.getStatusCode());
    }

    @Test(priority = 8, description = "Verify handling of zero as book ID")
    @Story("Get Book By ID")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify GET /api/v1/Books/{id} with ID = 0")
    public void testGetBookById_ZeroId() {
        int zeroId = 0;

        Response response = booksApiClient.getBookById(zeroId);

        // FakeRestAPI returns 404 for zero ID
        response.then()
                .statusCode(404);

        log.info("Tested with zero ID: {}, Status Code: {}", zeroId, response.getStatusCode());
    }

    @Test(priority = 9, description = "Verify creating book with null/missing title")
    @Story("Create Book")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify POST /api/v1/Books with null title (field omitted from JSON) should return 400")
    public void testCreateBook_NullTitle() {
        Book bookWithNullTitle = TestDataGenerator.generateRandomBook();
        bookWithNullTitle.setTitle(null);

        Response response = booksApiClient.createBook(bookWithNullTitle);

        // Expected: 400 Bad Request (title is a required field and should not be missing)
        // Actual: When title is null in Java, Jackson omits the field from JSON,
        // and FakeRestAPI incorrectly accepts missing title field and returns 200 - this is a bug
        response.then()
                .statusCode(400);

        log.info("Tested creating book with null/missing title field. Expected: 400, Actual: {}", response.getStatusCode());
    }

    @Test(priority = 10, description = "Verify creating book with empty string title")
    @Story("Create Book")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify POST /api/v1/Books with empty string title should return 400")
    public void testCreateBook_EmptyStringTitle() {
        Book bookWithEmptyTitle = TestDataGenerator.generateRandomBook();
        bookWithEmptyTitle.setTitle("");

        Response response = booksApiClient.createBook(bookWithEmptyTitle);

        // Expected: 400 Bad Request (empty string "" title should be rejected)
        // Actual: FakeRestAPI incorrectly returns 200 - this is a bug in the API
        response.then()
                .statusCode(400);

        log.info("Tested creating book with empty string title. Expected: 400, Actual: {}", response.getStatusCode());
    }

    @Test(priority = 11, description = "Verify creating book with null/missing description")
    @Story("Create Book")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify POST /api/v1/Books with null description (field omitted from JSON)")
    public void testCreateBook_NullDescription() {
        Book bookWithNullDesc = TestDataGenerator.generateRandomBook();
        bookWithNullDesc.setDescription(null);

        Response response = booksApiClient.createBook(bookWithNullDesc);

        // When description is null in Java, Jackson omits the field from JSON
        // FakeRestAPI accepts missing description field and returns 200
        response.then()
                .statusCode(200);

        log.info("Tested creating book with null/missing description field, Status Code: {}", response.getStatusCode());
    }

    @Test(priority = 12, description = "Verify creating book with empty string description")
    @Story("Create Book")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify POST /api/v1/Books with empty string description (optional field)")
    public void testCreateBook_EmptyStringDescription() {
        Book bookWithEmptyDesc = TestDataGenerator.generateRandomBook();
        bookWithEmptyDesc.setDescription("");

        Response response = booksApiClient.createBook(bookWithEmptyDesc);

        // Description is an optional field - empty string is acceptable
        // FakeRestAPI correctly accepts empty description and returns 200
        response.then()
                .statusCode(200);

        log.info("Tested creating book with empty string description (optional field), Status Code: {}", response.getStatusCode());
    }

    @Test(priority = 13, description = "Verify creating book with empty string excerpt")
    @Story("Create Book")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify POST /api/v1/Books with empty string excerpt (optional field)")
    public void testCreateBook_EmptyStringExcerpt() {
        Book bookWithEmptyExcerpt = TestDataGenerator.generateRandomBook();
        bookWithEmptyExcerpt.setExcerpt("");

        Response response = booksApiClient.createBook(bookWithEmptyExcerpt);

        // Excerpt is an optional field - empty string is acceptable
        // FakeRestAPI correctly accepts empty excerpt and returns 200
        response.then()
                .statusCode(200);

        log.info("Tested creating book with empty string excerpt (optional field), Status Code: {}", response.getStatusCode());
    }

    @Test(priority = 14, description = "Verify creating book with invalid publishDate")
    @Story("Create Book")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify POST /api/v1/Books with empty string publishDate should return 400")
    public void testCreateBook_InvalidPublishDate() {
        Book bookWithInvalidDate = TestDataGenerator.generateRandomBook();
        bookWithInvalidDate.setPublishDate("");

        Response response = booksApiClient.createBook(bookWithInvalidDate);

        // Expected: 400 Bad Request (empty date string is invalid)
        // FakeRestAPI correctly rejects empty date strings
        response.then()
                .statusCode(400);

        log.info("Tested creating book with empty publishDate. Expected: 400, Actual: {}", response.getStatusCode());
    }

    @Test(priority = 15, description = "Verify creating book with null publishDate")
    @Story("Create Book")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify POST /api/v1/Books with null publishDate should return 400")
    public void testCreateBook_NullPublishDate() {
        Book bookWithNullDate = TestDataGenerator.generateRandomBook();
        bookWithNullDate.setPublishDate(null);

        Response response = booksApiClient.createBook(bookWithNullDate);

        // Expected: 400 Bad Request (null publishDate should be rejected)
        // FakeRestAPI correctly returns 400
        response.then()
                .statusCode(400);

        log.info("Tested creating book with null publishDate. Expected: 400, Actual: {}", response.getStatusCode());
    }

    @Test(priority = 16, description = "Verify creating book with negative page count")
    @Story("Create Book")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify POST /api/v1/Books with negative page count should return 400")
    public void testCreateBook_NegativePageCount() {
        Book bookWithNegativePages = TestDataGenerator.generateRandomBook();
        bookWithNegativePages.setPageCount(-100);

        Response response = booksApiClient.createBook(bookWithNegativePages);

        // Expected: 400 Bad Request (negative page count should be rejected)
        // Actual: FakeRestAPI incorrectly returns 200 - this is a bug in the API
        response.then()
                .statusCode(400);

        log.info("Tested creating book with negative page count. Expected: 400, Actual: {}", response.getStatusCode());
    }

    @Test(priority = 17, description = "Verify creating book with zero page count")
    @Story("Create Book")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify POST /api/v1/Books with zero page count")
    public void testCreateBook_ZeroPageCount() {
        Book bookWithZeroPages = TestDataGenerator.generateRandomBook();
        bookWithZeroPages.setPageCount(0);

        Response response = booksApiClient.createBook(bookWithZeroPages);

        // Zero page count might be acceptable (e.g., empty book, pamphlet)
        // Or it might be invalid depending on business rules
        // FakeRestAPI accepts zero page count
        response.then()
                .statusCode(200);

        log.info("Tested creating book with zero page count, Status Code: {}", response.getStatusCode());
    }

    @Test(priority = 18, description = "Verify creating book with duplicate ID")
    @Story("Create Book")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify POST /api/v1/Books with duplicate ID should return 409 or 400")
    public void testCreateBook_DuplicateId() {
        // Create first book
        Book firstBook = TestDataGenerator.generateRandomBook();
        int duplicateId = firstBook.getId();

        Response firstResponse = booksApiClient.createBook(firstBook);
        firstResponse.then().statusCode(200);

        log.info("Created first book with ID: {}", duplicateId);

        // Try to create second book with same ID
        Book secondBook = TestDataGenerator.generateBookWithId(duplicateId);
        secondBook.setTitle("Different Title - " + System.currentTimeMillis());
        secondBook.setDescription("Different description");

        Response secondResponse = booksApiClient.createBook(secondBook);

        // Expected: 409 Conflict or 400 Bad Request (duplicate ID should be rejected)
        // ID should be unique, cannot create two resources with same ID
        secondResponse.then()
                .statusCode(anyOf(is(400), is(409)));

        log.info("Attempted to create book with duplicate ID: {}. Expected: 400/409, Actual: {}",
                duplicateId, secondResponse.getStatusCode());
    }

    @Test(priority = 19, description = "Verify creating book with very large page count")
    @Story("Create Book")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test boundary condition with very large page count")
    public void testCreateBook_VeryLargePageCount() {
        Book bookWithLargePages = TestDataGenerator.generateRandomBook();
        bookWithLargePages.setPageCount(Integer.MAX_VALUE);

        Response response = booksApiClient.createBook(bookWithLargePages);

        // FakeRestAPI accepts Integer.MAX_VALUE page count
        response.then()
                .statusCode(200);

        log.info("Tested creating book with very large page count: {}, Status Code: {}", Integer.MAX_VALUE, response.getStatusCode());
    }

    @Test(priority = 20, description = "Verify creating book with very long title")
    @Story("Create Book")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test boundary condition with very long title (1000 characters)")
    public void testCreateBook_VeryLongTitle() {
        Book bookWithLongTitle = TestDataGenerator.generateRandomBook();
        bookWithLongTitle.setTitle(TestDataGenerator.generateLongString(1000));

        Response response = booksApiClient.createBook(bookWithLongTitle);

        // FakeRestAPI accepts very long titles (1000 chars)
        response.then()
                .statusCode(200);

        log.info("Tested creating book with very long title (1000 chars), Status Code: {}", response.getStatusCode());
    }

    @Test(priority = 21, description = "Verify updating non-existent book")
    @Story("Update Book")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify PUT /api/v1/Books/{id} with non-existent ID should return 404")
    public void testUpdateBook_NonExistentId() {
        int nonExistentId = 999999;
        Book book = TestDataGenerator.generateBookWithId(nonExistentId);

        Response response = booksApiClient.updateBook(nonExistentId, book);

        // Expected: 404 Not Found (can't update non-existent resource)
        // Actual: FakeRestAPI incorrectly returns 200 - this is a bug in the API
        response.then()
                .statusCode(404);

        log.info("Tested updating non-existent book ID: {}. Expected: 404, Actual: {}", nonExistentId, response.getStatusCode());
    }

    @Test(priority = 22, description = "Verify updating book with mismatched ID")
    @Story("Update Book")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify PUT /api/v1/Books/{id} when path ID doesn't match body ID should return 400")
    public void testUpdateBook_MismatchedId() {
        int pathId = 1;
        int bodyId = 999;
        Book book = TestDataGenerator.generateBookWithId(bodyId);

        Response response = booksApiClient.updateBook(pathId, book);

        // Expected: 400 Bad Request (path ID and body ID must match)
        // When updating a resource, the ID in the URL should match the ID in the request body
        // Actual: FakeRestAPI incorrectly accepts mismatched IDs and returns 200 - this is a bug
        response.then()
                .statusCode(400);

        log.info("Tested updating book with mismatched IDs - Path: {}, Body: {}. Expected: 400, Actual: {}",
                pathId, bodyId, response.getStatusCode());
    }

    @Test(priority = 23, description = "Verify deleting non-existent book")
    @Story("Delete Book")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify DELETE /api/v1/Books/{id} with non-existent ID should return 404")
    public void testDeleteBook_NonExistentId() {
        int nonExistentId = 999999;

        Response response = booksApiClient.deleteBook(nonExistentId);

        // Expected: 404 Not Found (can't delete non-existent resource)
        // Actual: FakeRestAPI incorrectly returns 200 - this is a bug in the API
        response.then()
                .statusCode(404);

        log.info("Tested deleting non-existent book ID: {}. Expected: 404, Actual: {}", nonExistentId, response.getStatusCode());
    }

    @Test(priority = 24, description = "Verify deleting book with negative ID")
    @Story("Delete Book")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test to verify DELETE /api/v1/Books/{id} with negative ID should return 404")
    public void testDeleteBook_NegativeId() {
        int negativeId = -1;

        Response response = booksApiClient.deleteBook(negativeId);

        // Expected: 404 Not Found or 400 Bad Request (invalid ID)
        // Actual: FakeRestAPI incorrectly returns 200 - this is a bug in the API
        response.then()
                .statusCode(404);

        log.info("Tested deleting book with negative ID: {}. Expected: 404, Actual: {}", negativeId, response.getStatusCode());
    }

    @Test(priority = 25, description = "Verify response time for getting all books")
    @Story("Performance Test")
    @Severity(SeverityLevel.MINOR)
    @Description("Test to verify GET /api/v1/Books responds within acceptable time")
    public void testGetAllBooks_ResponseTime() {
        Response response = booksApiClient.getAllBooks();

        response.then()
                .statusCode(200)
                .time(lessThan(5000L)); // 5 seconds

        log.info("Response time for getting all books: {} ms", response.time());
    }

    @Test(priority = 26, description = "Verify book data structure integrity")
    @Story("Data Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify book object has all required fields with correct data types")
    public void testGetBookById_DataStructureValidation() {
        int bookId = 1;
        Response response = booksApiClient.getBookById(bookId);

        Book book = response.as(Book.class);

        // Validate all fields exist and have correct data types
        assertThat(book.getId()).isNotNull().isInstanceOf(Integer.class);
        assertThat(book.getTitle()).isNotNull().isInstanceOf(String.class);
        assertThat(book.getDescription()).isNotNull().isInstanceOf(String.class);
        assertThat(book.getPageCount()).isNotNull().isInstanceOf(Integer.class);
        assertThat(book.getExcerpt()).isNotNull().isInstanceOf(String.class);
        assertThat(book.getPublishDate()).isNotNull().isInstanceOf(String.class);

        log.info("Book data structure validated successfully for ID: {} - All 6 fields present with correct types, Status Code: {}",
                bookId, response.getStatusCode());
    }
}
