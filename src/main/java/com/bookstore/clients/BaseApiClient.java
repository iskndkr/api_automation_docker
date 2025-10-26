package com.bookstore.clients;

import com.bookstore.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

/**
 * Base API Client with common REST operations
 * Follows Single Responsibility Principle - handles HTTP communication
 */
@Slf4j
public abstract class BaseApiClient {

    protected ConfigManager config;
    protected RequestSpecification requestSpec;

    protected BaseApiClient() {
        this.config = ConfigManager.getInstance();
        initializeRequestSpec();
    }

    /**
     * Initialize request specification with base configuration
     */
    private void initializeRequestSpec() {
        RestAssured.baseURI = config.getBaseUrl();

        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .log(LogDetail.ALL)
                .build();

        log.info("Request specification initialized with base URL: {}", config.getBaseUrl());
    }

    /**
     * Generic GET request
     */
    protected Response get(String endpoint) {
        log.info("Sending GET request to: {}", endpoint);
        return RestAssured
                .given(requestSpec)
                .when()
                .get(endpoint);
    }

    /**
     * Generic GET request with path parameter
     */
    protected Response get(String endpoint, Object id) {
        String fullEndpoint = endpoint + "/" + id;
        log.info("Sending GET request to: {}", fullEndpoint);
        return RestAssured
                .given(requestSpec)
                .when()
                .get(fullEndpoint);
    }

    /**
     * Generic POST request
     */
    protected Response post(String endpoint, Object body) {
        log.info("Sending POST request to: {} with body: {}", endpoint, body);
        return RestAssured
                .given(requestSpec)
                .body(body)
                .when()
                .post(endpoint);
    }

    /**
     * Generic PUT request
     */
    protected Response put(String endpoint, Object id, Object body) {
        String fullEndpoint = endpoint + "/" + id;
        log.info("Sending PUT request to: {} with body: {}", fullEndpoint, body);
        return RestAssured
                .given(requestSpec)
                .body(body)
                .when()
                .put(fullEndpoint);
    }

    /**
     * Generic DELETE request
     */
    protected Response delete(String endpoint, Object id) {
        String fullEndpoint = endpoint + "/" + id;
        log.info("Sending DELETE request to: {}", fullEndpoint);
        return RestAssured
                .given(requestSpec)
                .when()
                .delete(fullEndpoint);
    }

    /**
     * Get base URL for logging/reporting purposes
     */
    public String getBaseUrl() {
        return config.getBaseUrl();
    }
}
