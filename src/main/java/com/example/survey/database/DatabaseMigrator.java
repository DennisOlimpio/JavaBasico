package com.example.survey.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseMigrator {

    private static final String CREATE_SURVEY_TABLE = """
            CREATE TABLE IF NOT EXISTS surveys (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT
            );
            """;

    private static final String CREATE_OPTION_TABLE = """
            CREATE TABLE IF NOT EXISTS survey_options (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                survey_id INTEGER NOT NULL,
                label TEXT NOT NULL,
                FOREIGN KEY (survey_id) REFERENCES surveys(id) ON DELETE CASCADE
            );
            """;

    private static final String CREATE_VOTE_TABLE = """
            CREATE TABLE IF NOT EXISTS survey_votes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                option_id INTEGER NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (option_id) REFERENCES survey_options(id) ON DELETE CASCADE
            );
            """;

    public void migrate(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_SURVEY_TABLE);
            statement.executeUpdate(CREATE_OPTION_TABLE);
            statement.executeUpdate(CREATE_VOTE_TABLE);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialize database schema", e);
        }
    }
}
