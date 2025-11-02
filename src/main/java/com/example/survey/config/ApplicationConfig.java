package com.example.survey.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class ApplicationConfig {
    private final Properties properties = new Properties();

    public ApplicationConfig() {
        loadProperties("application.properties");
    }

    public ApplicationConfig(String resourceName) {
        loadProperties(resourceName);
    }

    private void loadProperties(String resourceName) {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
            if (input == null) {
                throw new IllegalStateException("Config file not found: " + resourceName);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load config file", e);
        }
    }

    public String getDatabaseType() {
        return properties.getProperty("db.type", "sqlite");
    }

    public String getProperty(String key) {
        return Objects.requireNonNull(properties.getProperty(key), "Missing config property: " + key);
    }
}
