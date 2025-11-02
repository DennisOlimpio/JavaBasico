package com.example.survey.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class PostgresDataSourceProvider implements DataSourceProvider {
    private final DataSource dataSource;

    public PostgresDataSourceProvider(String jdbcUrl, String user, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        config.setPoolName("PostgresPool");
        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}
