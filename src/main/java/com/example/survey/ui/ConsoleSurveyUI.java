package com.example.survey.ui;

import com.example.survey.domain.Survey;
import com.example.survey.domain.SurveyOption;
import com.example.survey.domain.SurveyResult;
import com.example.survey.service.SurveyService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleSurveyUI implements SurveyUI {

    private final SurveyService surveyService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleSurveyUI(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @Override
    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Survey Application ===");
            System.out.println("1. Criar nova pesquisa");
            System.out.println("2. Listar pesquisas");
            System.out.println("3. Votar em uma pesquisa");
            System.out.println("4. Dashboard");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opcao: ");

            switch (scanner.nextLine()) {
                case "1" -> createSurvey();
                case "2" -> listSurveys();
                case "3" -> vote();
                case "4" -> showDashboard();
                case "0" -> running = false;
                default -> System.out.println("Opcao invalida");
            }
        }
    }

    private void createSurvey() {
        System.out.print("Titulo da pesquisa: ");
        String title = scanner.nextLine();
        System.out.print("Descricao: ");
        String description = scanner.nextLine();
        Survey survey = new Survey(title, description);

        while (true) {
            System.out.print("Adicionar opcao (deixe vazio para finalizar): ");
            String option = scanner.nextLine();
            if (option.isBlank()) {
                break;
            }
            survey.addOption(new SurveyOption(option));
        }
        if (survey.getOptions().isEmpty()) {
            System.out.println("Uma pesquisa precisa de pelo menos uma opcao.");
            return;
        }
        Survey saved = surveyService.createSurvey(survey);
        System.out.println("Pesquisa criada com ID: " + saved.getId());
    }

    private void listSurveys() {
        List<Survey> surveys = surveyService.listSurveys();
        if (surveys.isEmpty()) {
            System.out.println("Nenhuma pesquisa cadastrada.");
            return;
        }
        System.out.println("\n--- Pesquisas ---");
        for (Survey survey : surveys) {
            System.out.printf("[%d] %s - %s%n", survey.getId(), survey.getTitle(), survey.getDescription());
            for (SurveyOption option : survey.getOptions()) {
                System.out.printf("    (%d) %s%n", option.getId(), option.getLabel());
            }
        }
    }

    private void vote() {
        System.out.print("ID da pesquisa: ");
        int surveyId = Integer.parseInt(scanner.nextLine());
        Optional<Survey> surveyOptional = surveyService.getSurvey(surveyId);
        if (surveyOptional.isEmpty()) {
            System.out.println("Pesquisa nao encontrada.");
            return;
        }
        Survey survey = surveyOptional.get();
        System.out.println("Pesquisando: " + survey.getTitle());
        for (SurveyOption option : survey.getOptions()) {
            System.out.printf("(%d) %s%n", option.getId(), option.getLabel());
        }
        System.out.print("Escolha o ID da opcao: ");
        int optionId = Integer.parseInt(scanner.nextLine());
        surveyService.vote(optionId);
        System.out.println("Voto registrado!");
    }

    private void showDashboard() {
        List<SurveyResult> results = surveyService.dashboard();
        if (results.isEmpty()) {
            System.out.println("Nenhum voto registrado ainda.");
            return;
        }
        System.out.println("\n--- Dashboard ---");
        int currentSurvey = -1;
        for (SurveyResult result : results) {
            if (currentSurvey != result.getSurveyId()) {
                currentSurvey = result.getSurveyId();
                System.out.println("Pesquisa: " + result.getSurveyTitle());
            }
            System.out.printf("    %s -> %d votos%n", result.getOptionLabel(), result.getVotes());
        }
    }
}
