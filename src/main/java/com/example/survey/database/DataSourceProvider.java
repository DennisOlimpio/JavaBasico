package com.example.survey.database;

import javax.sql.DataSource;

public interface DataSourceProvider {
    DataSource getDataSource();
}
