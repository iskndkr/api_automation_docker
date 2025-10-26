package com.bookstore.clients;

import com.bookstore.models.Author;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Authors API Client
 * Provides methods for interacting with the Authors API endpoints
 */
@Slf4j
public class AuthorsApiClient extends BaseApiClient {

    private final String authorsEndpoint;

    public AuthorsApiClient() {
        super();
        this.authorsEndpoint = config.getAuthorsEndpoint();
    }

    @Step("Get all authors")
    public Response getAllAuthors() {
        log.info("Retrieving all authors");
        return get(authorsEndpoint);
    }

    @Step("Get author by ID: {id}")
    public Response getAuthorById(int id) {
        log.info("Retrieving author with ID: {}", id);
        return get(authorsEndpoint, id);
    }

    @Step("Create new author")
    public Response createAuthor(Author author) {
        log.info("Creating new author: {} {}", author.getFirstName(), author.getLastName());
        return post(authorsEndpoint, author);
    }

    @Step("Update author with ID: {id}")
    public Response updateAuthor(int id, Author author) {
        log.info("Updating author with ID: {}", id);
        return put(authorsEndpoint, id, author);
    }

    @Step("Delete author with ID: {id}")
    public Response deleteAuthor(int id) {
        log.info("Deleting author with ID: {}", id);
        return delete(authorsEndpoint, id);
    }
}
