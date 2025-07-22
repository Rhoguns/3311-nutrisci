/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import com.nutrisci.dao.DAOFactory;
import com.nutrisci.service.AnalysisModule;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

public class CfgCompliancePanel extends JPanel {
    private final JTextField tfProfileId = new JTextField("1", 8);
    private final JSpinner spinnerDate;
    private final JButton btnLoad = new JButton("Load CFG Analysis");
    private final JPanel chartContainer = new JPanel(new GridLayout(1, 2, 10, 10));
    private final JLabel statusLabel = new JLabel("Ready - Enter profile ID and date to analyze CFG compliance.");
    private final AnalysisModule analysis = new AnalysisModule(DAOFactory.getNutritionDAO());

    public CfgCompliancePanel() {
        this.spinnerDate = makeDateSpinner();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(createTopPanel(), BorderLayout.NORTH);
        add(chartContainer, BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
        
        chartContainer.add(new JLabel("CFG charts will appear here after loading data.", SwingConstants.CENTER));
        btnLoad.addActionListener(this::loadCfg);
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
        // Set default date to 2025-07-19 for UC7 testing
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2025, java.util.Calendar.JULY, 19);
        
        SpinnerDateModel model = new SpinnerDateModel(cal.getTime(), null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        return spinner;
    }

    private void loadCfg(ActionEvent ev) {
        try {
            int pid = Integer.parseInt(tfProfileId.getText().trim());
            Date d = (Date) spinnerDate.getValue();
            LocalDate day = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            statusLabel.setText("Analyzing CFG compliance for profile " + pid + " on " + day + "...");
            
            Map<String, Double> actual = analysis.computeCfgCompliance(pid, day);
            double totalActual = actual.values().stream().mapToDouble(Double::doubleValue).sum();
            
            if (totalActual == 0.0) {
                statusLabel.setText("No meal data found for the selected profile and date.");
                chartContainer.removeAll();
                chartContainer.add(new JLabel("No data available for CFG analysis.", SwingConstants.CENTER));
                chartContainer.revalidate();
                chartContainer.repaint();
                return;
            }

            // Canada Food Guide ideal percentages
            Map<String, Double> ideal = Map.of(
                "Vegetables and Fruits", 50.0,
                "Grain Products", 25.0,
                "Milk and Alternatives", 12.5,
                "Meat and Alternatives", 12.5
            );

            updateCharts(actual, ideal, day);
            statusLabel.setText("CFG analysis completed successfully.");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Profile ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading CFG data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateCharts(Map<String, Double> actual, Map<String, Double> ideal, LocalDate date) {
        chartContainer.removeAll();
        
        // Create JFreeChart pie charts
        ChartPanel actualChartPanel = createJFreeChartPiePanel("Actual CFG Distribution - " + date, actual);
        ChartPanel idealChartPanel = createJFreeChartPiePanel("Ideal CFG Distribution", ideal);
        
        chartContainer.add(actualChartPanel);
        chartContainer.add(idealChartPanel);
        
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    private ChartPanel createJFreeChartPiePanel(String title, Map<String, Double> data) {
        // Create dataset
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        
        // Calculate total for percentage calculation
        double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
        
        // Add data to dataset with percentages
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double percentage = (entry.getValue() / total) * 100;
            dataset.setValue(entry.getKey() + " (" + String.format("%.1f%%", percentage) + ")", percentage);
        }
        
        // Create the chart
        JFreeChart chart = ChartFactory.createPieChart(
            title,           // chart title
            dataset,         // data
            true,           // include legend
            true,           // tooltips
            false           // urls
        );
        
        // Customize the plot
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setSectionOutlinesVisible(true);
        plot.setOutlineStroke(new BasicStroke(1.0f));
        
        // Set colors for CFG categories
        plot.setSectionPaint("Vegetables and Fruits", new Color(76, 175, 80));     // Green
        plot.setSectionPaint("Grain Products", new Color(255, 193, 7));            // Amber
        plot.setSectionPaint("Milk and Alternatives", new Color(33, 150, 243));    // Blue
        plot.setSectionPaint("Meat and Alternatives", new Color(244, 67, 54));     // Red
        
        // Handle entries with percentage labels
        for (String key : data.keySet()) {
            String keyWithPercent = key + " (" + String.format("%.1f%%", (data.get(key) / data.values().stream().mapToDouble(Double::doubleValue).sum()) * 100) + ")";
            if (key.contains("Vegetables")) {
                plot.setSectionPaint(keyWithPercent, new Color(76, 175, 80));
            } else if (key.contains("Grain")) {
                plot.setSectionPaint(keyWithPercent, new Color(255, 193, 7));
            } else if (key.contains("Milk")) {
                plot.setSectionPaint(keyWithPercent, new Color(33, 150, 243));
            } else if (key.contains("Meat")) {
                plot.setSectionPaint(keyWithPercent, new Color(244, 67, 54));
            }
        }
        
        // Create and return ChartPanel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.setBorder(BorderFactory.createEtchedBorder());
        
        return chartPanel;
    }

    public void setProfile(int profileId) {
        tfProfileId.setText(String.valueOf(profileId));
        loadCfg(null);
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
