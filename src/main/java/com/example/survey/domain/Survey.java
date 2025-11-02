package com.example.survey.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Survey {
    private Integer id;
    private String title;
    private String description;
    private final List<SurveyOption> options = new ArrayList<>();

    public Survey(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SurveyOption> getOptions() {
        return options;
    }

    public void addOption(SurveyOption option) {
        Objects.requireNonNull(option, "option");
        options.add(option);
    }
}
