package com.example.survey.service;

import com.example.survey.domain.Survey;
import com.example.survey.domain.SurveyResult;
import com.example.survey.repository.SurveyRepository;

import java.util.List;
import java.util.Optional;

public class SurveyService {

    private final SurveyRepository repository;

    public SurveyService(SurveyRepository repository) {
        this.repository = repository;
    }

    public Survey createSurvey(Survey survey) {
        return repository.save(survey);
    }

    public List<Survey> listSurveys() {
        return repository.findAll();
    }

    public Optional<Survey> getSurvey(int id) {
        return repository.findById(id);
    }

    public void vote(int optionId) {
        repository.recordVote(optionId);
    }

    public List<SurveyResult> dashboard() {
        return repository.fetchResults();
    }
}
