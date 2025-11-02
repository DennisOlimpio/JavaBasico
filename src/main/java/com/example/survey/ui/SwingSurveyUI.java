package com.example.survey.ui;

import com.example.survey.domain.Survey;
import com.example.survey.domain.SurveyOption;
import com.example.survey.domain.SurveyResult;
import com.example.survey.service.SurveyService;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SwingSurveyUI extends JFrame implements SurveyUI {

    private final SurveyService surveyService;

    private final DefaultListModel<Survey> surveyListModel = new DefaultListModel<>();
    private final JList<Survey> surveyList = new JList<>(surveyListModel);

    private final DefaultListModel<SurveyOption> optionListModel = new DefaultListModel<>();
    private final JList<SurveyOption> optionList = new JList<>(optionListModel);

    private final DefaultListModel<String> newSurveyOptionsModel = new DefaultListModel<>();
    private final JList<String> newSurveyOptionsList = new JList<>(newSurveyOptionsModel);

    private final JTextField titleField = new JTextField();
    private final JTextArea descriptionArea = new JTextArea(4, 20);
    private final JTextField optionInput = new JTextField();

    private final JTextArea dashboardArea = new JTextArea();

    public SwingSurveyUI(SurveyService surveyService) {
        this.surveyService = surveyService;
        buildUI();
    }

    private void buildUI() {
        setTitle("Survey Application");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(900, 600));
        setMinimumSize(new Dimension(900, 600));

        surveyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        surveyList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Survey survey) {
                    setText(String.format("[%d] %s", survey.getId(), survey.getTitle()));
                }
                return this;
            }
        });

        optionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        optionList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SurveyOption option) {
                    String prefix = option.getId() != null ? option.getId().toString() : String.valueOf(index + 1);
                    setText(String.format("(%s) %s", prefix, option.getLabel()));
                }
                return this;
            }
        });

        newSurveyOptionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        surveyList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedSurveyOptions();
            }
        });

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Pesquisas", createSurveyPanel());
        tabs.addTab("Criar Pesquisa", createCreatePanel());
        tabs.addTab("Dashboard", createDashboardPanel());

        add(tabs, BorderLayout.CENTER);
        pack();
    }

    private JPanel createSurveyPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JButton refreshButton = new JButton("Atualizar");
        refreshButton.addActionListener(e -> {
            refreshSurveys();
            refreshDashboard();
        });
        panel.add(refreshButton, BorderLayout.NORTH);

        panel.add(new JScrollPane(surveyList), BorderLayout.CENTER);

        JPanel sidePanel = new JPanel(new BorderLayout(5, 5));
        sidePanel.add(new JScrollPane(optionList), BorderLayout.CENTER);

        JButton voteButton = new JButton("Votar");
        voteButton.addActionListener(e -> voteOnSelectedOption());
        sidePanel.add(voteButton, BorderLayout.SOUTH);

        panel.add(sidePanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCreatePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        panel.add(new JLabel("Titulo"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Descricao"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        panel.add(descriptionScroll, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Opcao"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(optionInput, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        JPanel optionButtons = new JPanel();
        JButton addOptionButton = new JButton("Adicionar opcao");
        addOptionButton.addActionListener(e -> addOptionToNewSurvey());
        JButton removeOptionButton = new JButton("Remover selecionada");
        removeOptionButton.addActionListener(e -> removeOptionFromNewSurvey());
        optionButtons.add(addOptionButton);
        optionButtons.add(removeOptionButton);
        panel.add(optionButtons, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        panel.add(new JScrollPane(newSurveyOptionsList), gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        JButton saveButton = new JButton("Salvar pesquisa");
        saveButton.addActionListener(e -> saveSurvey());
        panel.add(saveButton, gbc);

        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        dashboardArea.setEditable(false);
        dashboardArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        panel.add(new JScrollPane(dashboardArea), BorderLayout.CENTER);
        return panel;
    }

    private void loadSelectedSurveyOptions() {
        optionListModel.clear();
        Survey selected = surveyList.getSelectedValue();
        if (selected == null) {
            return;
        }
        for (SurveyOption option : selected.getOptions()) {
            optionListModel.addElement(option);
        }
    }

    private void voteOnSelectedOption() {
        Survey selectedSurvey = surveyList.getSelectedValue();
        SurveyOption selectedOption = optionList.getSelectedValue();
        if (selectedSurvey == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma pesquisa primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedOption == null || selectedOption.getId() == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma opcao valida para votar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        surveyService.vote(selectedOption.getId());
        JOptionPane.showMessageDialog(this, "Voto registrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        refreshDashboard();
        refreshSurveys();
    }

    private void addOptionToNewSurvey() {
        String optionText = optionInput.getText().trim();
        if (optionText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o texto da opcao.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        newSurveyOptionsModel.addElement(optionText);
        optionInput.setText("");
    }

    private void removeOptionFromNewSurvey() {
        int selectedIndex = newSurveyOptionsList.getSelectedIndex();
        if (selectedIndex >= 0) {
            newSurveyOptionsModel.remove(selectedIndex);
        }
    }

    private void saveSurvey() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O titulo da pesquisa e obrigatorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (newSurveyOptionsModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione pelo menos uma opcao.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Survey survey = new Survey(title, description);
        for (int i = 0; i < newSurveyOptionsModel.size(); i++) {
            String label = newSurveyOptionsModel.get(i);
            survey.addOption(new SurveyOption(label));
        }

        surveyService.createSurvey(survey);

        JOptionPane.showMessageDialog(this, "Pesquisa criada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        titleField.setText("");
        descriptionArea.setText("");
        newSurveyOptionsModel.clear();

        refreshSurveys();
    }

    private void refreshSurveys() {
        Survey currentSelection = surveyList.getSelectedValue();
        Integer selectedId = currentSelection != null ? currentSelection.getId() : null;

        surveyListModel.clear();
        List<Survey> surveys = surveyService.listSurveys();

        int indexToSelect = -1;
        for (int i = 0; i < surveys.size(); i++) {
            Survey survey = surveys.get(i);
            surveyListModel.addElement(survey);
            if (selectedId != null && selectedId.equals(survey.getId())) {
                indexToSelect = i;
            }
        }

        if (indexToSelect >= 0) {
            surveyList.setSelectedIndex(indexToSelect);
        } else if (!surveyListModel.isEmpty()) {
            surveyList.setSelectedIndex(0);
        } else {
            optionListModel.clear();
        }
    }

    private void refreshDashboard() {
        List<SurveyResult> results = surveyService.dashboard();
        if (results.isEmpty()) {
            dashboardArea.setText("Nenhum voto registrado ainda.");
            return;
        }

        Map<String, List<SurveyResult>> grouped = new LinkedHashMap<>();
        for (SurveyResult result : results) {
            grouped.computeIfAbsent(result.getSurveyTitle(), k -> new ArrayList<>()).add(result);
        }

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, List<SurveyResult>> entry : grouped.entrySet()) {
            builder.append("Pesquisa: ").append(entry.getKey()).append('\n');
            for (SurveyResult result : entry.getValue()) {
                builder.append("    ").append(result.getOptionLabel()).append(" -> ")
                        .append(result.getVotes()).append(" votos").append('\n');
            }
            builder.append('\n');
        }
        dashboardArea.setText(builder.toString());
        dashboardArea.setCaretPosition(0);
    }

    @Override
    public void start() {
        SwingUtilities.invokeLater(() -> {
            refreshSurveys();
            refreshDashboard();
            setLocationRelativeTo(null);
            setVisible(true);
        });
    }
}
