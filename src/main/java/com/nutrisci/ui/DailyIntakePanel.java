package com.nutrisci.ui;

import com.nutrisci.dao.DAOFactory;
import com.nutrisci.service.AnalysisModule;
import com.nutrisci.service.AnalysisModule.DailySummary;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import java.awt.Color; 
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class DailyIntakePanel extends JPanel {
    private JTextField tfProfileId = new JTextField(8);
    private JTextField tfFromDate = new JTextField(LocalDate.now().minusDays(7).toString(), 12);
    private JTextField tfToDate = new JTextField(LocalDate.now().toString(), 12);
    private JButton btnLoad = new JButton("Load Data");
    private JLabel statusLabel = new JLabel("Ready - Enter profile and date range to load data.");
    private JTable intakeTable = new JTable();
    private JPanel chartContainer = new JPanel(new GridLayout(1, 2, 10, 10));
    private DefaultTableModel tableModel;
    private ChartPanel calorieChartPanel;
    private ChartPanel macroChartPanel;

    private AnalysisModule analysisModule = new AnalysisModule(DAOFactory.getNutritionDAO());
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DailyIntakePanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(createTopPanel(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(intakeTable), chartContainer);
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);

        add(createStatusPanel(), BorderLayout.SOUTH);

        chartContainer.add(new JLabel("Charts will appear here after loading data.", SwingConstants.CENTER));

        btnLoad.addActionListener(this::loadDailyIntake);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.add(new JLabel("Profile ID:"));
        panel.add(tfProfileId);
        panel.add(new JLabel("Start Date:"));
        panel.add(tfFromDate);
        panel.add(new JLabel("End Date:"));
        panel.add(tfToDate);
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

    private void loadDailyIntake(ActionEvent ev) {
        try {
            int profileId = Integer.parseInt(tfProfileId.getText().trim());
            LocalDate fromDate = LocalDate.parse(tfFromDate.getText().trim(), dateFormatter);
            LocalDate toDate = LocalDate.parse(tfToDate.getText().trim(), dateFormatter);

            if (fromDate.isAfter(toDate)) {
                throw new IllegalArgumentException("Start Date cannot be after End Date.");
            }

            statusLabel.setText("Loading daily intake data for profile " + profileId + "...");
            
            List<DailySummary> summaries = analysisModule.getDailyIntakeSummary(profileId, fromDate, toDate);

            if (summaries.isEmpty()) {
                statusLabel.setText("No meal data found for the selected profile and date range. Use Meal Logger to add meals first.");
                clearCharts();
                return;
            }

            updateTable(summaries);
            updateCharts(summaries);
            statusLabel.setText("Successfully loaded " + summaries.size() + " days of data.");

        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }
    
    private void clearCharts() {
        tableModel.setRowCount(0);
        
        DefaultCategoryDataset emptyDataset = new DefaultCategoryDataset();
        JFreeChart emptyChart = ChartFactory.createLineChart(
            "No Data Available - Add meals to see daily calorie trends", 
            "Date", "Calories", emptyDataset);
        calorieChartPanel.setChart(emptyChart);
        
        DefaultPieDataset<String> emptyPieDataset = new DefaultPieDataset<>();
        emptyPieDataset.setValue("No Data", 1.0);
        JFreeChart emptyPieChart = ChartFactory.createPieChart(
            "Add meals to see macronutrient breakdown", 
            emptyPieDataset, false, false, false);
        macroChartPanel.setChart(emptyPieChart);
    }

    private void updateTable(List<DailySummary> summaries) {
        String[] columnNames = {"Date", "Calories", "Protein (g)", "Carbs (g)", "Fat (g)", "Fibre (g)", "Cal %RDI", "Pro %RDI", "Carb %RDI", "Fat %RDI", "Fibre %RDI"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (DailySummary day : summaries) {
            Object[] row = {
                day.getDate().format(dateFormatter),
                String.format("%.0f", day.getTotalCalories()),
                String.format("%.1f", day.getTotalProtein()),
                String.format("%.1f", day.getTotalCarbs()),
                String.format("%.1f", day.getTotalFat()),
                String.format("%.1f", day.getTotalFibre()), 
                String.format("%.1f%%", day.getCaloriesRdiPercent()),
                String.format("%.1f%%", day.getProteinRdiPercent()),
                String.format("%.1f%%", day.getCarbsRdiPercent()),
                String.format("%.1f%%", day.getFatRdiPercent()),
                String.format("%.1f%%", day.getFibreRdiPercent()) 
            };
            model.addRow(row);
        }
        intakeTable.setModel(model);
    }

    private void updateCharts(List<AnalysisModule.DailySummary> summaries) {
        chartContainer.removeAll();

        if (summaries.isEmpty()) {
            chartContainer.add(new JLabel("No data to display charts.", SwingConstants.CENTER));
            chartContainer.revalidate();
            chartContainer.repaint();
            return;
        }

        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
        for (AnalysisModule.DailySummary day : summaries) {
            lineDataset.addValue(day.getTotalCalories(), "Calories", 
                day.getDate().format(DateTimeFormatter.ofPattern("MM-dd")));
        }
        JFreeChart lineChart = ChartFactory.createLineChart(
            "Daily Calorie Intake", "Date", "Calories (kcal)", lineDataset);

        lineChart.getCategoryPlot().getRangeAxis().setLowerBound(0);

        chartContainer.add(new ChartPanel(lineChart));

        double totalProtein = 0.0;
        double totalCarbs = 0.0;
        double totalFat = 0.0;
        
        for (AnalysisModule.DailySummary summary : summaries) {
            totalProtein += summary.getTotalProtein();
            totalCarbs += summary.getTotalCarbs();
            totalFat += summary.getTotalFat();
        }

        if (totalProtein + totalCarbs + totalFat == 0) {
            chartContainer.add(new JLabel("No macronutrient data available.", SwingConstants.CENTER));
        } else {
            DefaultPieDataset<String> pieDataset = new DefaultPieDataset<>();
            String proteinKey = String.format("Protein (%.0fg)", totalProtein);
            String carbsKey = String.format("Carbohydrates (%.0fg)", totalCarbs);
            String fatKey = String.format("Fat (%.0fg)", totalFat);

            pieDataset.setValue(proteinKey, totalProtein);
            pieDataset.setValue(carbsKey, totalCarbs);
            pieDataset.setValue(fatKey, totalFat);

            JFreeChart pieChart = ChartFactory.createPieChart(
                "Total Macronutrient Breakdown (grams)", pieDataset, true, true, false);

            @SuppressWarnings("unchecked")
            PiePlot<String> plot = (PiePlot<String>) pieChart.getPlot();
            plot.setSectionPaint(proteinKey, new Color(79, 129, 189));
            plot.setSectionPaint(carbsKey, new Color(192, 80, 77));
            plot.setSectionPaint(fatKey, new Color(155, 187, 89));
            plot.setSimpleLabels(true);

            chartContainer.add(new ChartPanel(pieChart));
        }

        chartContainer.revalidate();
        chartContainer.repaint();
    }
    
    public void setProfile(int profileId) {
        tfProfileId.setText(String.valueOf(profileId));
        statusLabel.setText("Profile set to: " + profileId + ". Enter date range and click Load Data.");
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                
            }
            
            JFrame frame = new JFrame("Daily Intake Panel Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            DailyIntakePanel panel = new DailyIntakePanel();
            frame.add(panel);
            
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            
            
        });
    }
}
