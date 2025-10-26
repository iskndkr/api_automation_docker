package com.bookstore.clients;

import com.bookstore.models.Book;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Books API Client
 * Provides methods for interacting with the Books API endpoints
 */
@Slf4j
public class BooksApiClient extends BaseApiClient {

    private final String booksEndpoint;

    public BooksApiClient() {
        super();
        this.booksEndpoint = config.getBooksEndpoint();
    }

    @Step("Get all books")
    public Response getAllBooks() {
        log.info("Retrieving all books");
        return get(booksEndpoint);
    }

    @Step("Get book by ID: {id}")
    public Response getBookById(int id) {
        log.info("Retrieving book with ID: {}", id);
        return get(booksEndpoint, id);
    }

    @Step("Create new book")
    public Response createBook(Book book) {
        log.info("Creating new book: {}", book.getTitle());
        return post(booksEndpoint, book);
    }

    @Step("Update book with ID: {id}")
    public Response updateBook(int id, Book book) {
        log.info("Updating book with ID: {}", id);
        return put(booksEndpoint, id, book);
    }

    @Step("Delete book with ID: {id}")
    public Response deleteBook(int id) {
        log.info("Deleting book with ID: {}", id);
        return delete(booksEndpoint, id);
    }
}
