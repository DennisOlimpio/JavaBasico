package com.example.survey.domain;

public class SurveyResult {
    private final int surveyId;
    private final String surveyTitle;
    private final String optionLabel;
    private final long votes;

    public SurveyResult(int surveyId, String surveyTitle, String optionLabel, long votes) {
        this.surveyId = surveyId;
        this.surveyTitle = surveyTitle;
        this.optionLabel = optionLabel;
        this.votes = votes;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public String getSurveyTitle() {
        return surveyTitle;
    }

    public String getOptionLabel() {
        return optionLabel;
    }

    public long getVotes() {
        return votes;
    }
}
