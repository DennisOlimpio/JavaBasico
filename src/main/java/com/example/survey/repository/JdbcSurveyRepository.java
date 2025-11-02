package com.example.survey.repository;

import com.example.survey.domain.Survey;
import com.example.survey.domain.SurveyOption;
import com.example.survey.domain.SurveyResult;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcSurveyRepository implements SurveyRepository {

    private final DataSource dataSource;

    public JdbcSurveyRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Survey save(Survey survey) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                Integer surveyId = insertSurvey(connection, survey);
                survey.setId(surveyId);
                for (SurveyOption option : survey.getOptions()) {
                    insertOption(connection, surveyId, option);
                }
                connection.commit();
                return survey;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save survey", e);
        }
    }

    private Integer insertSurvey(Connection connection, Survey survey) throws SQLException {
        String sql = "INSERT INTO surveys(title, description) VALUES(?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, survey.getTitle());
            ps.setString(2, survey.getDescription());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Failed to retrieve survey id");
            }
        }
    }

    private void insertOption(Connection connection, int surveyId, SurveyOption option) throws SQLException {
        String sql = "INSERT INTO survey_options(survey_id, label) VALUES(?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, surveyId);
            ps.setString(2, option.getLabel());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    option.setId(rs.getInt(1));
                    option.setSurveyId(surveyId);
                }
            }
        }
    }

    @Override
    public List<Survey> findAll() {
        String sql = "SELECT id, title, description FROM surveys ORDER BY id";
        List<Survey> surveys = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Survey survey = new Survey(rs.getString("title"), rs.getString("description"));
                survey.setId(rs.getInt("id"));
                survey.getOptions().addAll(findOptionsBySurveyId(connection, survey.getId()));
                surveys.add(survey);
            }
            return surveys;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch surveys", e);
        }
    }

    @Override
    public Optional<Survey> findById(int id) {
        String sql = "SELECT id, title, description FROM surveys WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Survey survey = new Survey(rs.getString("title"), rs.getString("description"));
                    survey.setId(rs.getInt("id"));
                    survey.getOptions().addAll(findOptionsBySurveyId(connection, id));
                    return Optional.of(survey);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch survey", e);
        }
    }

    private List<SurveyOption> findOptionsBySurveyId(Connection connection, int surveyId) throws SQLException {
        String sql = "SELECT id, label FROM survey_options WHERE survey_id = ? ORDER BY id";
        List<SurveyOption> options = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, surveyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SurveyOption option = new SurveyOption(rs.getString("label"));
                    option.setId(rs.getInt("id"));
                    option.setSurveyId(surveyId);
                    options.add(option);
                }
            }
        }
        return options;
    }

    @Override
    public void recordVote(int optionId) {
        String sql = "INSERT INTO survey_votes(option_id) VALUES (?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, optionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to record vote", e);
        }
    }

    @Override
    public List<SurveyResult> fetchResults() {
        String sql = """
                SELECT s.id AS survey_id, s.title AS survey_title, o.label AS option_label, COUNT(v.id) AS votes
                FROM surveys s
                JOIN survey_options o ON s.id = o.survey_id
                LEFT JOIN survey_votes v ON o.id = v.option_id
                GROUP BY s.id, s.title, o.label
                ORDER BY s.id, votes DESC
                """;
        List<SurveyResult> results = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new SurveyResult(
                        rs.getInt("survey_id"),
                        rs.getString("survey_title"),
                        rs.getString("option_label"),
                        rs.getLong("votes")));
            }
            return results;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch results", e);
        }
    }
}
