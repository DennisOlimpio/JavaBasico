package com.example.survey.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.nio.file.Path;

public class SQLiteDataSourceProvider implements DataSourceProvider {
    private final DataSource dataSource;

    public SQLiteDataSourceProvider(String jdbcUrl) {
        ensureDirectory(jdbcUrl);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(5);
        config.setPoolName("SQLitePool");
        this.dataSource = new HikariDataSource(config);
    }

    private void ensureDirectory(String jdbcUrl) {
        if (jdbcUrl.startsWith("jdbc:sqlite:")) {
            String pathString = jdbcUrl.substring("jdbc:sqlite:".length());
            Path path = Path.of(pathString).toAbsolutePath();
            Path parent = path.getParent();
            if (parent != null) {
                parent.toFile().mkdirs();
            }
        }
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}
