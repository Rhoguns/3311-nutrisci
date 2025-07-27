package com.nutrisci.ui;

import com.nutrisci.dao.DAOFactory;
import com.nutrisci.dao.NutritionDAO;
import com.nutrisci.logic.SwapEngine;
import com.nutrisci.model.NutrientTotals;
import com.nutrisci.model.SwapRule;
import com.nutrisci.service.AnalysisModule;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SwapImpactPanel extends JPanel {
    private int profileId = 1;
    private AnalysisModule analysisModule;
    private SwapEngine swapEngine;
    
    // UI Components
    private JSpinner startDateSpinner, endDateSpinner;
    private JSpinner singleDateSpinner;
    private JComboBox<SwapRule> swapRuleComboBox;
    private JPanel chartPanel;
    private JTabbedPane tabbedPane;
    private JLabel swapProfileLabel, timeSeriesProfileLabel, beforeAfterProfileLabel;

    private NutritionDAO nutritionDao = DAOFactory.getNutritionDAO();

    public SwapImpactPanel() {
        analysisModule = new AnalysisModule(nutritionDao);
        swapEngine = new SwapEngine(DAOFactory.getSwapRuleDAO(), DAOFactory.getMealDAO());
        
        initializeUI();
        loadSwapRules();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane();
        
        // Tab 1: Apply Swap Over Time
        JPanel applyPanel = createApplyOverTimePanel();
        tabbedPane.addTab("Apply Over Time", applyPanel);
        
        // Tab 2: Time Series Analysis
        JPanel timeSeriesPanel = createTimeSeriesPanel();
        tabbedPane.addTab("Time Series", timeSeriesPanel);
        
        // Tab 3: Before/After Analysis
        JPanel beforeAfterPanel = createBeforeAfterPanel();
        tabbedPane.addTab("Before/After", beforeAfterPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createApplyOverTimePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Control panel
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Profile ID:"), gbc);
        gbc.gridx = 1;
        swapProfileLabel = new JLabel(String.valueOf(profileId));
        controlPanel.add(swapProfileLabel, gbc);
        
        // Swap Rule selection
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("Swap Rule:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        swapRuleComboBox = new JComboBox<>();
        swapRuleComboBox.setPreferredSize(new Dimension(300, 25));
        controlPanel.add(swapRuleComboBox, gbc);
        
        // Date range
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        controlPanel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JULY, 1);
        startDateSpinner = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, Calendar.DAY_OF_MONTH));
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        controlPanel.add(startDateSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        controlPanel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1;
        cal.set(2025, Calendar.JULY, 19);
        endDateSpinner = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, Calendar.DAY_OF_MONTH));
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
        controlPanel.add(endDateSpinner, gbc);
        
        // Apply button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton applyButton = new JButton("Apply Over Time");
        applyButton.setPreferredSize(new Dimension(200, 30));
        applyButton.addActionListener(e -> applyOverTimeAction());
        controlPanel.add(applyButton, gbc);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        
        // Results area
        JTextArea resultsArea = new JTextArea(15, 50);
        resultsArea.setEditable(false);
        resultsArea.setText("Select a swap rule and date range, then click 'Apply Over Time' to begin.\n\n" +
                           "This will:\n" +
                           "1. Find all meals in the date range\n" +
                           "2. Identify meals containing the original food\n" +
                           "3. Replace the original food with the suggested food\n" +
                           "4. Update the database with the changes\n\n" +
                           "Results will appear here...");
        panel.add(new JScrollPane(resultsArea), BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createTimeSeriesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        // Control panel for time series
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Profile ID: "));
        timeSeriesProfileLabel = new JLabel(String.valueOf(profileId));
        controlPanel.add(timeSeriesProfileLabel);
        controlPanel.add(Box.createHorizontalStrut(20));
        
        controlPanel.add(new JLabel("Start Date: "));
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JULY, 1);
        JSpinner tsStartSpinner = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, Calendar.DAY_OF_MONTH));
        tsStartSpinner.setEditor(new JSpinner.DateEditor(tsStartSpinner, "yyyy-MM-dd"));
        controlPanel.add(tsStartSpinner);
        
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(new JLabel("End Date: "));
        cal.set(2025, Calendar.JULY, 19);
        JSpinner tsEndSpinner = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, Calendar.DAY_OF_MONTH));
        tsEndSpinner.setEditor(new JSpinner.DateEditor(tsEndSpinner, "yyyy-MM-dd"));
        controlPanel.add(tsEndSpinner);
        
        JButton loadTimeSeriesButton = new JButton("Load Time Series");
        loadTimeSeriesButton.addActionListener(e -> loadTimeSeries(tsStartSpinner, tsEndSpinner, panel));
        controlPanel.add(loadTimeSeriesButton);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        
        // Chart panel
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("Daily Δ-Calories Due to Swaps"));
        chartPanel.add(new JLabel("Select date range and click 'Load Time Series' to view daily calorie changes.", SwingConstants.CENTER), BorderLayout.CENTER);
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createBeforeAfterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Profile ID: "));
        beforeAfterProfileLabel = new JLabel(String.valueOf(profileId));
        controlPanel.add(beforeAfterProfileLabel);
        controlPanel.add(Box.createHorizontalStrut(20));
        
        controlPanel.add(new JLabel("Date: "));
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JULY, 19);
        singleDateSpinner = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, Calendar.DAY_OF_MONTH));
        controlPanel.add(singleDateSpinner);
        
        JButton loadBeforeAfterButton = new JButton("Load Before/After");
        loadBeforeAfterButton.addActionListener(e -> loadBeforeAfter(panel));
        controlPanel.add(loadBeforeAfterButton);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        
        // Chart panel
        JPanel baChartPanel = new JPanel(new BorderLayout());
        baChartPanel.setBorder(BorderFactory.createTitledBorder("Before vs After Swap Nutrients"));
        baChartPanel.add(new JLabel("Select a date and click 'Load Before/After' to view comparison.", SwingConstants.CENTER), BorderLayout.CENTER);
        panel.add(baChartPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void loadTimeSeries(JSpinner startSpinner, JSpinner endSpinner, JPanel parentPanel) {
        try {
            Date startDate = (Date) startSpinner.getValue();
            Date endDate = (Date) endSpinner.getValue();
            LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            // Get daily calorie deltas due to swaps
            Map<LocalDate, Double> dailyDeltas = analysisModule.getDailySwapCalorieDeltas(this.profileId, start, end);
            
            if (dailyDeltas.isEmpty()) {
                chartPanel.removeAll();
                chartPanel.add(new JLabel("No swap data found for the selected date range.", SwingConstants.CENTER), BorderLayout.CENTER);
                chartPanel.revalidate();
                chartPanel.repaint();
                return;
            }
            
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (Map.Entry<LocalDate, Double> entry : dailyDeltas.entrySet()) {
                dataset.addValue(entry.getValue(), "Δ-Calories", entry.getKey().toString());
            }
            
            JFreeChart chart = ChartFactory.createLineChart(
                "Daily Calorie Changes Due to Swaps",
                "Date",
                "Calorie Change (kcal)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
            );
            
            // Customize chart
            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.GRAY);
            
            LineAndShapeRenderer renderer = new LineAndShapeRenderer();
            renderer.setSeriesPaint(0, Color.BLUE);
            renderer.setSeriesShapesVisible(0, true);
            plot.setRenderer(renderer);
            
            // Update chart panel
            chartPanel.removeAll();
            ChartPanel cp = new ChartPanel(chart);
            cp.setPreferredSize(new Dimension(800, 400));
            chartPanel.add(cp, BorderLayout.CENTER);
            chartPanel.revalidate();
            chartPanel.repaint();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading time series: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadBeforeAfter(JPanel parentPanel) {
        try {
            Date selectedDate = (Date) singleDateSpinner.getValue();
            LocalDate analysisDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            Map<String, NutrientTotals> results = analysisModule.computeSwapBeforeAfter(profileId, analysisDate);
            
            if (results.isEmpty() || !results.containsKey("before") || !results.containsKey("after")) {
                JPanel baChartPanel = findChartPanelInTab(parentPanel);
                baChartPanel.removeAll();
                baChartPanel.add(new JLabel("No swap data found for the selected date.", SwingConstants.CENTER), BorderLayout.CENTER);
                baChartPanel.revalidate();
                baChartPanel.repaint();
                return;
            }
            
            NutrientTotals before = results.get("before");
            NutrientTotals after = results.get("after");
            
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            dataset.addValue(before.getCalories(), "Before", "Calories");
            dataset.addValue(after.getCalories(), "After", "Calories");
            dataset.addValue(before.getProtein(), "Before", "Protein (g)");
            dataset.addValue(after.getProtein(), "After", "Protein (g)");
            dataset.addValue(before.getCarbs(), "Before", "Carbs (g)");
            dataset.addValue(after.getCarbs(), "After", "Carbs (g)");
            dataset.addValue(before.getFat(), "Before", "Fat (g)");
            dataset.addValue(after.getFat(), "After", "Fat (g)");
            
            JFreeChart chart = ChartFactory.createBarChart(
                "Before vs After Swap - " + analysisDate,
                "Nutrients",
                "Amount",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
            );
            
            // Customize chart
            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.GRAY);
            
            BarRenderer renderer = new BarRenderer();
            renderer.setSeriesPaint(0, new Color(255, 102, 102)); // Light red for "Before"
            renderer.setSeriesPaint(1, new Color(102, 255, 102)); // Light green for "After"
            plot.setRenderer(renderer);
            
            // Update chart panel
            JPanel baChartPanel = findChartPanelInTab(parentPanel);
            baChartPanel.removeAll();
            ChartPanel cp = new ChartPanel(chart);
            cp.setPreferredSize(new Dimension(800, 400));
            baChartPanel.add(cp, BorderLayout.CENTER);
            baChartPanel.revalidate();
            baChartPanel.repaint();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading before/after analysis: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private JPanel findChartPanelInTab(JPanel tab) {
        // Find the chart panel with the titled border
        for (Component comp : tab.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getBorder() instanceof javax.swing.border.TitledBorder) {
                    return panel;
                }
            }
        }
        return null;
    }

    private void loadSwapRules() {
        try {
            List<SwapRule> rules = DAOFactory.getSwapRuleDAO().findAll();
            DefaultComboBoxModel<SwapRule> model = new DefaultComboBoxModel<>();
            for (SwapRule rule : rules) {
                model.addElement(rule);
            }
            swapRuleComboBox.setModel(model);
            
            swapRuleComboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof SwapRule) {
                        SwapRule rule = (SwapRule) value;
                        setText(String.format("ID %d: %s → %s (%s)", rule.getId(), rule.getOriginalFood(), rule.getSuggestedFood(), rule.getGoal()));
                    }
                    return this;
                }
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading swap rules: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    public void setProfile(int profileId) {
        this.profileId = profileId;
        updateProfileLabels();
    }

    private void updateProfileLabels() {
        String profileText = String.valueOf(profileId);
        swapProfileLabel.setText(profileText);
        timeSeriesProfileLabel.setText(profileText);  
        beforeAfterProfileLabel.setText(profileText);
    }

    private void applyOverTimeAction() {
        SwapRule selectedRule = (SwapRule) swapRuleComboBox.getSelectedItem();
        if (selectedRule == null) {
            JOptionPane.showMessageDialog(this, "Please select a swap rule.", "No Rule Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Date startDate = (Date) startDateSpinner.getValue();
            Date endDate = (Date) endDateSpinner.getValue();
            LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            int updatedCount = swapEngine.applySwapRuleOverTime(profileId, selectedRule.getId(), start, end);

            JTextArea resultsArea = findResultsArea();
            resultsArea.setText(String.format(
                "Applied swap rule over time\n\n" +
                "Rule: %s → %s\n" +
                "Goal: %s\n" +
                "Profile ID: %d\n" +
                "Date Range: %s to %s\n" +
                "Meals Updated: %d\n\n" +
                "All meals containing '%s' in the specified date range\n" +
                "have been updated to use '%s' instead.\n\n" +
                "The changes have been saved to the database.",
                selectedRule.getOriginalFood(),
                selectedRule.getSuggestedFood(),
                selectedRule.getGoal(),
                profileId,
                start,
                end,
                updatedCount,
                selectedRule.getOriginalFood(),
                selectedRule.getSuggestedFood()
            ));

            JOptionPane.showMessageDialog(this,
                String.format("Successfully applied swap to %d meals.", updatedCount),
                "Swap Applied",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JTextArea resultsArea = findResultsArea();
            resultsArea.setText("❌ ERROR: Failed to apply swap\n\n" +
                              "Error: " + ex.getMessage() + "\n\n" +
                              "Please check:\n" +
                              "- Database connection\n" +
                              "- Swap rule exists\n" +
                              "- Date range is valid\n" +
                              "- Profile has meals in date range");
            JOptionPane.showMessageDialog(this,
                "Error applying swap: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private JTextArea findResultsArea() {
        return findComponentOfType(this, JTextArea.class);
    }

    private <T> T findComponentOfType(Container container, Class<T> type) {
        for (Component comp : container.getComponents()) {
            if (type.isInstance(comp)) {
                return type.cast(comp);
            } else if (comp instanceof Container) {
                T found = findComponentOfType((Container) comp, type);
                if (found != null) return found;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            JFrame frame = new JFrame("Swap Impact Panel - UC5 & UC8");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            SwapImpactPanel panel = new SwapImpactPanel();
            panel.setProfile(1);
            frame.add(panel);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
        });
    }
}
