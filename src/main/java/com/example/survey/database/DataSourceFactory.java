package com.example.survey.database;

import com.example.survey.config.ApplicationConfig;

public class DataSourceFactory {

    public static DataSourceProvider fromConfig(ApplicationConfig config) {
        String type = config.getDatabaseType();
        return switch (type.toLowerCase()) {
            case "postgres", "postgresql" -> new PostgresDataSourceProvider(
                    config.getProperty("postgres.url"),
                    config.getProperty("postgres.user"),
                    config.getProperty("postgres.password"));
            case "sqlite" -> new SQLiteDataSourceProvider(config.getProperty("sqlite.url"));
            default -> throw new IllegalArgumentException("Unsupported database type: " + type);
        };
    }
}
