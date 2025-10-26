package com.bookstore.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton class to manage application configuration
 * Reads configuration from config.properties and environment variables
 */
@Slf4j
public class ConfigManager {

    private static ConfigManager instance;
    private final Properties properties;

    private ConfigManager() {
        properties = new Properties();
        loadProperties();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                log.error("Unable to find config.properties");
                return;
            }
            properties.load(input);
            log.info("Configuration loaded successfully");
        } catch (IOException e) {
            log.error("Error loading configuration: {}", e.getMessage());
        }
    }

    /**
     * Get property value, with support for environment variable override
     * Environment variables take precedence over properties file
     */
    public String getProperty(String key) {
        // Check environment variable first (convert to uppercase and replace dots with underscores)
        String envKey = key.toUpperCase().replace(".", "_");
        String envValue = System.getenv(envKey);

        if (envValue != null && !envValue.isEmpty()) {
            log.debug("Using environment variable {} for key {}", envKey, key);
            return envValue;
        }

        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public String getBaseUrl() {
        return getProperty("base.url");
    }

    public String getApiVersion() {
        return getProperty("api.version");
    }

    public String getBooksEndpoint() {
        return getApiVersion() + getProperty("books.endpoint");
    }

    public String getAuthorsEndpoint() {
        return getApiVersion() + getProperty("authors.endpoint");
    }

    public int getRequestTimeout() {
        return Integer.parseInt(getProperty("request.timeout", "30000"));
    }

    public int getConnectionTimeout() {
        return Integer.parseInt(getProperty("connection.timeout", "10000"));
    }
}
