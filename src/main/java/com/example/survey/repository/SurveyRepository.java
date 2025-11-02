package com.example.survey.repository;

import com.example.survey.domain.Survey;
import com.example.survey.domain.SurveyResult;

import java.util.List;
import java.util.Optional;

public interface SurveyRepository {
    Survey save(Survey survey);

    List<Survey> findAll();

    Optional<Survey> findById(int id);

    void recordVote(int optionId);

    List<SurveyResult> fetchResults();
}
