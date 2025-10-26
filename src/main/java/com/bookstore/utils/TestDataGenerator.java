package com.bookstore.utils;

import com.bookstore.models.Author;
import com.bookstore.models.Book;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Utility class for generating test data
 * Helps create realistic test objects for API testing
 */
public class TestDataGenerator {

    private static final Random random = new Random();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * Generate a random book with valid data
     */
    public static Book generateRandomBook() {
        int id = random.nextInt(10000);
        return Book.builder()
                .id(id)
                .title("Test Book " + id)
                .description("This is a test book description for book " + id)
                .pageCount(100 + random.nextInt(900))
                .excerpt("This is an excerpt from test book " + id)
                .publishDate(LocalDateTime.now().format(formatter))
                .build();
    }

    /**
     * Generate a book with specific ID
     */
    public static Book generateBookWithId(int id) {
        return Book.builder()
                .id(id)
                .title("Test Book " + id)
                .description("This is a test book description for book " + id)
                .pageCount(100 + random.nextInt(900))
                .excerpt("This is an excerpt from test book " + id)
                .publishDate(LocalDateTime.now().format(formatter))
                .build();
    }

    /**
     * Generate a book with invalid data (for negative testing)
     */
    public static Book generateInvalidBook() {
        return Book.builder()
                .id(-1)
                .title("")
                .description(null)
                .pageCount(-100)
                .excerpt(null)
                .publishDate("invalid-date")
                .build();
    }

    /**
     * Generate a random author with valid data
     */
    public static Author generateRandomAuthor() {
        int id = random.nextInt(10000);
        return Author.builder()
                .id(id)
                .idBook(random.nextInt(200))
                .firstName("FirstName" + id)
                .lastName("LastName" + id)
                .build();
    }

    /**
     * Generate an author with specific ID
     */
    public static Author generateAuthorWithId(int id) {
        return Author.builder()
                .id(id)
                .idBook(random.nextInt(200))
                .firstName("FirstName" + id)
                .lastName("LastName" + id)
                .build();
    }

    /**
     * Generate an author with invalid data (for negative testing)
     */
    public static Author generateInvalidAuthor() {
        return Author.builder()
                .id(-1)
                .idBook(-1)
                .firstName("")
                .lastName(null)
                .build();
    }

    /**
     * Generate a very long string for boundary testing
     */
    public static String generateLongString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("a");
        }
        return sb.toString();
    }
}
