package com.example.survey;

import com.example.survey.config.ApplicationConfig;
import com.example.survey.database.DataSourceFactory;
import com.example.survey.database.DatabaseMigrator;
import com.example.survey.database.DataSourceProvider;
import com.example.survey.repository.JdbcSurveyRepository;
import com.example.survey.service.SurveyService;
import com.example.survey.ui.ConsoleSurveyUI;
import com.example.survey.ui.SurveyUI;

import javax.sql.DataSource;

public class Main {
    public static void main(String[] args) {
        ApplicationConfig config = new ApplicationConfig();
        DataSourceProvider provider = DataSourceFactory.fromConfig(config);
        DataSource dataSource = provider.getDataSource();

        new DatabaseMigrator().migrate(dataSource);

        SurveyService surveyService = new SurveyService(new JdbcSurveyRepository(dataSource));
        SurveyUI ui = new ConsoleSurveyUI(surveyService);
        ui.start();
    }
}
