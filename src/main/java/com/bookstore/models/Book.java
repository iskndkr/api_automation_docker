package com.bookstore.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Book model representing the Book entity from FakeRestAPI
 * Uses Lombok annotations for reducing boilerplate code
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("pageCount")
    private Integer pageCount;

    @JsonProperty("excerpt")
    private String excerpt;

    @JsonProperty("publishDate")
    private String publishDate;
}