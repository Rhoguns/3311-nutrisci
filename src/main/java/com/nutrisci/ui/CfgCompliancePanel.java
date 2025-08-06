package com.nutrisci.ui;

import com.nutrisci.dao.DAOFactory;
import com.nutrisci.service.AnalysisModule;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CfgCompliancePanel extends JPanel {
    private CfgInputPanel inputPanel;
    private CfgChartPanel chartPanel;
    private CfgComplianceCalculator calculator;
    private JLabel statusLabel;
    
    public CfgCompliancePanel() {
        this.calculator = new CfgComplianceCalculator();
        this.inputPanel = new CfgInputPanel();
        this.chartPanel = new CfgChartPanel();
        initializeLayout();
        setupEventHandlers();
    }
    
    private void initializeLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(inputPanel, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        inputPanel.setLoadButtonListener(this::handleLoadRequest);
    }
    
    private void handleLoadRequest(ActionEvent e) {
        try {
            CfgInputData input = inputPanel.getInputData();
            CfgComplianceData data = calculator.calculateCompliance(input);
            chartPanel.updateCharts(data);
            statusLabel.setText("CFG analysis complete");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusLabel = new JLabel("Ready - Enter profile ID and date to analyze CFG compliance.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        panel.add(statusLabel, BorderLayout.WEST);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JFrame frame = new JFrame("CFG Compliance Panel - UC7 Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            CfgCompliancePanel panel = new CfgCompliancePanel();
            frame.add(panel);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            JOptionPane.showMessageDialog(frame, 
                "UC7 Test Instructions:\n\n" +
                "1. Profile ID is set to 1 (default)\n" +
                "2. Date is set to 2025-07-19 (for test data)\n" +
                "3. Click 'Load CFG Analysis' to see results\n\n" +
                "Expected: JFreeChart pie charts showing actual vs ideal\n" +
                "Canada Food Guide percentages by category", 
                "Test Setup", 
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
}

class CfgInputData {
    private final int profileId;
    private final LocalDate date;
    
    public CfgInputData(int profileId, LocalDate date) {
        this.profileId = profileId;
        this.date = date;
    }
    
    public int getProfileId() {
        return profileId;
    }
    
    public LocalDate getDate() {
        return date;
    }
}

class CfgComplianceData {
    private final Map<String, Double> actualData;
    private final Map<String, Double> recommendedData;
    
    public CfgComplianceData(Map<String, Double> actualData, Map<String, Double> recommendedData) {
        this.actualData = actualData;
        this.recommendedData = recommendedData;
    }
    
    public Map<String, Double> getActualData() {
        return actualData;
    }
    
    public Map<String, Double> getRecommendedData() {
        return recommendedData;
    }
}

class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}


class CfgInputPanel extends JPanel {
    private JTextField tfProfileId;
    private JSpinner spinnerDate;
    private JButton btnLoad;
    private ActionListener loadListener;
    
    public CfgInputPanel() {
        initializeComponents();
        layoutComponents();
    }
    
    public CfgInputData getInputData() throws ValidationException {
        int profileId = Integer.parseInt(tfProfileId.getText().trim());
        LocalDate date = convertSpinnerToDate();
        return new CfgInputData(profileId, date);
    }
    
    public void setLoadButtonListener(ActionListener listener) {
        this.loadListener = listener;
        btnLoad.addActionListener(listener);
    }
    
    private void initializeComponents() {
        tfProfileId = new JTextField("1", 8);
        spinnerDate = makeDateSpinner();
        btnLoad = new JButton("Load CFG Analysis");
    }
    
    private void layoutComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        add(new JLabel("Profile ID:"));
        add(tfProfileId);
        add(new JLabel("Date:"));
        add(spinnerDate);
        add(btnLoad);
    }
    
    private JSpinner makeDateSpinner() {
       java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2025, java.util.Calendar.JULY, 19);
        
        SpinnerDateModel model = new SpinnerDateModel(cal.getTime(), null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        return spinner;
    }
}

class CfgChartPanel extends JPanel {
    private ChartPanel actualChartPanel;
    private ChartPanel recommendedChartPanel;
    
    public CfgChartPanel() {
        initializeCharts();
        layoutCharts();
    }
    
    public void updateCharts(CfgComplianceData data) {
        updateActualChart(data.getActualData());
        updateRecommendedChart(data.getRecommendedData());
        revalidate();
        repaint();
    }
    
    private void initializeCharts() {
        actualChartPanel = createPlaceholderChart("Actual CFG Distribution");
        recommendedChartPanel = createPlaceholderChart("Ideal CFG Distribution");
        
        setLayout(new GridLayout(1, 2, 10, 10));
        add(actualChartPanel);
        add(recommendedChartPanel);
    }
    
    private ChartPanel createPlaceholderChart(String title) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("No Data Available", 1.0);
        
        JFreeChart chart = ChartFactory.createPieChart(
            title + " (Click Load to Update)",
            dataset,
            true, true, false);
            
        chart.getPlot().setBackgroundPaint(Color.WHITE);
        if (chart.getPlot() instanceof PiePlot) {
            @SuppressWarnings("unchecked")
            PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
            plot.setSectionPaint("No Data Available", Color.LIGHT_GRAY);
        }
        
        return new ChartPanel(chart);
    }
    
    private void updateActualChart(Map<String, Double> actualData) {
        DefaultPieDataset<String> dataset = createDataset(actualData);
        JFreeChart chart = ChartFactory.createPieChart(
            "Actual CFG Distribution", dataset, true, true, false);
        actualChartPanel.setChart(chart);
    }
    
    private void updateRecommendedChart(Map<String, Double> recommendedData) {
        DefaultPieDataset<String> dataset = createDataset(recommendedData);
        JFreeChart chart = ChartFactory.createPieChart(
            "Ideal CFG Distribution", dataset, true, true, false);
        recommendedChartPanel.setChart(chart);
    }
    
    private DefaultPieDataset<String> createDataset(Map<String, Double> data) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            if (entry.getValue() > 0) { // Only add non-zero values
                dataset.setValue(entry.getKey(), entry.getValue());
            }
        }
        return dataset;
    }
}


class CfgComplianceCalculator {
    private AnalysisModule analysis;
    
    public CfgComplianceCalculator() {
        this.analysis = new AnalysisModule(DAOFactory.getNutritionDAO());
    }
    
    public CfgComplianceData calculateCompliance(CfgInputData input) throws SQLException {
        Map<String, Double> rawData = analysis.computeCfgCompliance(
            input.getProfileId(), input.getDate());
        
        return new CfgComplianceData(
            extractActualData(rawData),
            extractRecommendedData(rawData)
        );
    }
    
    private Map<String, Double> extractActualData(Map<String, Double> mixedData) {
        Map<String, Double> actualData = new HashMap<>();
        for (Map.Entry<String, Double> entry : mixedData.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            
            if (!key.endsWith("_recommended")) {
                actualData.put(key, value);
            }
        }
        return actualData;
    }
    
    private Map<String, Double> extractRecommendedData(Map<String, Double> mixedData) {
        Map<String, Double> recommendedData = new HashMap<>();
        for (Map.Entry<String, Double> entry : mixedData.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            
            if (key.endsWith("_recommended")) {
                String cleanKey = key.replace("_recommended", "");
                recommendedData.put(cleanKey, value);
            }
        }
        return recommendedData;
    }
}
