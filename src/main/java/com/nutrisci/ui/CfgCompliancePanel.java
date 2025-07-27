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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CfgCompliancePanel extends JPanel {
    private JTextField tfProfileId = new JTextField("1", 8);
    private JSpinner spinnerDate;
    private JButton btnLoad = new JButton("Load CFG Analysis");
    private JPanel chartContainer = new JPanel(new GridLayout(1, 2, 10, 10));
    private JLabel statusLabel = new JLabel("Ready - Enter profile ID and date to analyze CFG compliance.");
    private AnalysisModule analysis = new AnalysisModule(DAOFactory.getNutritionDAO());
    
    private ChartPanel actualChartPanel;
    private ChartPanel recommendedChartPanel;

    public CfgCompliancePanel() {
        spinnerDate = makeDateSpinner();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(createTopPanel(), BorderLayout.NORTH);
        add(chartContainer, BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
        initializeCharts();
        btnLoad.addActionListener(this::loadCfg);
    }
    
    private void initializeCharts() {
        actualChartPanel = createPlaceholderChart("Actual CFG Distribution");
        recommendedChartPanel = createPlaceholderChart("Ideal CFG Distribution");
        
        chartContainer.removeAll();
        chartContainer.add(actualChartPanel);
        chartContainer.add(recommendedChartPanel);
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

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.add(new JLabel("Profile ID:"));
        panel.add(tfProfileId);
        panel.add(new JLabel("Date:"));
        panel.add(spinnerDate);
        panel.add(btnLoad);
        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        panel.add(statusLabel, BorderLayout.WEST);
        return panel;
    }

    private JSpinner makeDateSpinner() {
       java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2025, java.util.Calendar.JULY, 19);
        
        SpinnerDateModel model = new SpinnerDateModel(cal.getTime(), null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        return spinner;
    }

    private void loadCfg(ActionEvent ev) {
        try {
            int profileId = Integer.parseInt(tfProfileId.getText().trim());
            
            Object spinnerValue = spinnerDate.getValue();
            LocalDate date;
            
            if (spinnerValue instanceof java.util.Date) {
                java.util.Date utilDate = (java.util.Date) spinnerValue;
                date = utilDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
            } else {
                throw new IllegalArgumentException("Unexpected spinner value type: " + spinnerValue.getClass().getName());
            }
            
            statusLabel.setText("Loading CFG data for Profile " + profileId + " on " + date + "...");
            
            Map<String, Double> mixedCfgData = analysis.computeCfgCompliance(profileId, date);
            
           Map<String, Double> actualData = new HashMap<>();
            Map<String, Double> recommendedData = new HashMap<>();
            
            for (Map.Entry<String, Double> entry : mixedCfgData.entrySet()) {
                String key = entry.getKey();
                Double value = entry.getValue();
                
                if (key.endsWith("_recommended")) {
                    String cleanKey = key.replace("_recommended", "");
                    recommendedData.put(cleanKey, value);
                } else {
                    actualData.put(key, value);
                }
            }
            
            double actualTotal = 0.0;
            for (Double value : actualData.values()) {
                actualTotal += value;
            }
            
            double recommendedTotal = 0.0;
            for (Double value : recommendedData.values()) {
                recommendedTotal += value;
            }
            
            if (actualTotal == 0 && recommendedTotal == 0) {
                statusLabel.setText("No CFG data found for " + date + ". Use Meal Logger to add meals for analysis.");
                clearCharts();
                return;
            }
            
            updateCharts(actualData, recommendedData, date);
            
            statusLabel.setText(String.format("CFG analysis complete: %.1f actual servings, %.1f recommended servings", 
                actualTotal, recommendedTotal));
            
        } catch (NumberFormatException ex) {
            statusLabel.setText("Invalid Profile ID");
        } catch (Exception ex) {
            statusLabel.setText("Error loading CFG data: " + ex.getMessage());
        }
    }
    
    private void clearCharts() {
        DefaultPieDataset<String> emptyDataset = new DefaultPieDataset<>();
        emptyDataset.setValue("Add meals to see CFG data", 1.0);
        
        JFreeChart emptyActualChart = ChartFactory.createPieChart(
            "Actual CFG Distribution (No Data)", emptyDataset, false, false, false);
        actualChartPanel.setChart(emptyActualChart);
        
        JFreeChart emptyRecommendedChart = ChartFactory.createPieChart(
            "Ideal CFG Distribution (No Data)", emptyDataset, false, false, false);
        recommendedChartPanel.setChart(emptyRecommendedChart);
    }
    
    public void setProfile(int profileId) {
        tfProfileId.setText(String.valueOf(profileId));
        spinnerDate.setValue(new java.util.Date()); 
        statusLabel.setText("Profile set to: " + profileId + ". Select date and click Load CFG Analysis.");
    }
    
    private void updateCharts(Map<String, Double> actualData, Map<String, Double> recommendedData, LocalDate date) {
        DefaultPieDataset<String> actualDataset = new DefaultPieDataset<>();
        for (Map.Entry<String, Double> entry : actualData.entrySet()) {
            if (entry.getValue() > 0) { // Only add non-zero values
                actualDataset.setValue(entry.getKey(), entry.getValue());
            }
        }
        
        JFreeChart actualChart = ChartFactory.createPieChart(
            "Actual CFG Distribution - " + date,
            actualDataset,
            true, true, false);
        
         actualChart.getPlot().setBackgroundPaint(Color.WHITE);
        actualChartPanel.setChart(actualChart);
        
        DefaultPieDataset<String> recommendedDataset = new DefaultPieDataset<>();
        for (Map.Entry<String, Double> entry : recommendedData.entrySet()) {
            if (entry.getValue() > 0) { // Only add non-zero values
                recommendedDataset.setValue(entry.getKey(), entry.getValue());
            }
        }
        
        JFreeChart recommendedChart = ChartFactory.createPieChart(
            "Ideal CFG Distribution",
            recommendedDataset,
            true, true, false);
            
         recommendedChart.getPlot().setBackgroundPaint(Color.WHITE);
        recommendedChartPanel.setChart(recommendedChart);
        
        chartContainer.revalidate();
        chartContainer.repaint();
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
