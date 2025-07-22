/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import com.nutrisci.dao.DAOFactory;
import com.nutrisci.model.NutrientTotals;
import com.nutrisci.service.AnalysisModule;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SwapImpactPanel
extends JPanel {
    private int profileId = 0;
    private AnalysisModule analysisModule = new AnalysisModule(DAOFactory.getNutritionDAO());
    private JSpinner dateSpinner;
    private JPanel chartPanel;

    public SwapImpactPanel() {
        this.initializeUI();
    }

    private void initializeUI() {
        this.setLayout(new BorderLayout());
        JPanel controlPanel = new JPanel(new FlowLayout(0));
        controlPanel.add(new JLabel("Profile ID: "));
        JLabel profileLabel = new JLabel(String.valueOf(this.profileId));
        controlPanel.add(profileLabel);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(new JLabel("Date: "));
        Calendar cal = Calendar.getInstance();
        cal.set(2025, 6, 20);
        this.dateSpinner = new JSpinner(new SpinnerDateModel(cal.getTime(), null, null, 5));
        this.dateSpinner.setEditor(new JSpinner.DateEditor(this.dateSpinner, "yyyy-MM-dd"));
        controlPanel.add(this.dateSpinner);
        JButton analyzeButton = new JButton("Analyze Swap Impact");
        analyzeButton.addActionListener(new AnalyzeActionListener());
        controlPanel.add(analyzeButton);
        this.add((Component)controlPanel, "North");
        this.chartPanel = new JPanel(new BorderLayout());
        this.chartPanel.setBorder(BorderFactory.createTitledBorder("Before/After Swap Impact"));
        this.add((Component)this.chartPanel, "Center");
        this.chartPanel.add((Component)new JLabel("Select a date and click 'Analyze Swap Impact' to view results.", 0), "Center");
    }

    public void setProfile(int profileId) {
        Component[] components;
        this.profileId = profileId;
        Component[] componentArray = components = this.getComponents();
        int n = components.length;
        int n2 = 0;
        while (n2 < n) {
            Component comp = componentArray[n2];
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel)comp;
                Component[] componentArray2 = panel.getComponents();
                int n3 = componentArray2.length;
                int n4 = 0;
                while (n4 < n3) {
                    JLabel label;
                    Component subComp = componentArray2[n4];
                    if (subComp instanceof JLabel && (label = (JLabel)subComp).getText().matches("\\d+")) {
                        label.setText(String.valueOf(profileId));
                        break;
                    }
                    ++n4;
                }
            }
            ++n2;
        }
    }

    private void createComparisonChart(NutrientTotals nutrientTotals, NutrientTotals nutrientTotals2, LocalDate localDate) {
        throw new Error("Unresolved compilation problems: \n\tDefaultCategoryDataset cannot be resolved to a type\n\tDefaultCategoryDataset cannot be resolved to a type\n\tJFreeChart cannot be resolved to a type\n\tChartFactory cannot be resolved\n\tPlotOrientation cannot be resolved to a variable\n\tChartPanel cannot be resolved to a type\n\tChartPanel cannot be resolved to a type\n");
    }

    private JPanel createSummaryPanel(NutrientTotals before, NutrientTotals after) {
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Impact Summary"));
        double calorieDelta = after.getCalories() - before.getCalories();
        double proteinDelta = after.getProtein() - before.getProtein();
        double carbDelta = after.getCarbs() - before.getCarbs();
        double fatDelta = after.getFat() - before.getFat();
        panel.add(new JLabel("Calorie Change:"));
        panel.add(this.createDeltaLabel(calorieDelta, "cal"));
        panel.add(new JLabel("Protein Change:"));
        panel.add(this.createDeltaLabel(proteinDelta, "g"));
        panel.add(new JLabel("Carb Change:"));
        panel.add(this.createDeltaLabel(carbDelta, "g"));
        panel.add(new JLabel("Fat Change:"));
        panel.add(this.createDeltaLabel(fatDelta, "g"));
        return panel;
    }

    private JLabel createDeltaLabel(double delta, String unit) {
        String text = String.format("%+.1f %s", delta, unit);
        JLabel label = new JLabel(text);
        if (delta > 0.0) {
            label.setForeground(Color.RED);
        } else if (delta < 0.0) {
            label.setForeground(Color.GREEN);
        } else {
            label.setForeground(Color.BLACK);
        }
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception exception) {
                // empty catch block
            }
            JFrame frame = new JFrame("Swap Impact Panel - UC8 Test");
            frame.setDefaultCloseOperation(3);
            SwapImpactPanel panel = new SwapImpactPanel();
            panel.setProfile(1);
            frame.add(panel);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            JOptionPane.showMessageDialog(frame, "Test Instructions:\n1. Profile ID is pre-set to 1\n2. Use date: 2025-07-20 (has real swap data)\n3. Time Series: Use date range 2025-07-20 to 2025-07-20\n4. Before/After: Use single date 2025-07-20\n5. You should see real calorie/nutrient changes!", "Test Setup", 1);
        });
    }

    private class AnalyzeActionListener
    implements ActionListener {
        private AnalyzeActionListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (SwapImpactPanel.this.profileId <= 0) {
                JOptionPane.showMessageDialog(SwapImpactPanel.this, "Please select a valid profile first.", "No Profile Selected", 2);
                return;
            }
            try {
                Date selectedDate = (Date)SwapImpactPanel.this.dateSpinner.getValue();
                LocalDate analysisDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Map<String, NutrientTotals> results = SwapImpactPanel.this.analysisModule.computeSwapBeforeAfter(SwapImpactPanel.this.profileId, analysisDate);
                if (results.isEmpty() || !results.containsKey("before") || !results.containsKey("after")) {
                    JOptionPane.showMessageDialog(SwapImpactPanel.this, "No swap data found for the selected date.\nTry date: 2025-07-20 for test data.", "No Data", 1);
                    return;
                }
                SwapImpactPanel.this.createComparisonChart(results.get("before"), results.get("after"), analysisDate);
            }
            catch (SQLException ex) {
                JOptionPane.showMessageDialog(SwapImpactPanel.this, "Database error: " + ex.getMessage(), "Error", 0);
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(SwapImpactPanel.this, "Analysis error: " + ex.getMessage(), "Error", 0);
            }
        }
    }
}
