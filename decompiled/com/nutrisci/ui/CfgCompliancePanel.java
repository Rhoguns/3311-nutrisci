/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import com.nutrisci.dao.DAOFactory;
import com.nutrisci.service.AnalysisModule;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class CfgCompliancePanel
extends JPanel {
    private final JTextField tfProfileId = new JTextField(8);
    private final JSpinner spinnerDate;
    private final JButton btnLoad = new JButton("Load CFG Analysis");
    private final JPanel chartContainer = new JPanel(new GridLayout(1, 2, 10, 10));
    private final JLabel statusLabel = new JLabel("Ready - Enter profile ID and date to analyze CFG compliance.");
    private final AnalysisModule analysis = new AnalysisModule(DAOFactory.getNutritionDAO());

    public CfgCompliancePanel() {
        this.spinnerDate = this.makeDateSpinner();
        this.initComponents();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        this.add((Component)this.createTopPanel(), "North");
        this.add((Component)this.chartContainer, "Center");
        this.add((Component)this.createStatusPanel(), "South");
        this.chartContainer.add(new JLabel("CFG charts will appear here after loading data.", 0));
        this.btnLoad.addActionListener(this::loadCfg);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(0, 10, 5));
        panel.add(new JLabel("Profile ID:"));
        panel.add(this.tfProfileId);
        panel.add(new JLabel("Date:"));
        panel.add(this.spinnerDate);
        panel.add(this.btnLoad);
        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLoweredBevelBorder());
        this.statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        panel.add((Component)this.statusLabel, "West");
        return panel;
    }

    private JSpinner makeDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel(new Date(), null, null, 5);
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        return spinner;
    }

    private void loadCfg(ActionEvent ev) {
        try {
            int pid = Integer.parseInt(this.tfProfileId.getText().trim());
            Date d = (Date)this.spinnerDate.getValue();
            LocalDate day = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            this.statusLabel.setText("Analyzing CFG compliance for profile " + pid + " on " + String.valueOf(day) + "...");
            Map<String, Double> actual = this.analysis.computeCfgCompliance(pid, day);
            double totalActual = actual.values().stream().mapToDouble(Double::doubleValue).sum();
            if (totalActual == 0.0) {
                this.statusLabel.setText("No meal data found for the selected profile and date.");
                this.chartContainer.removeAll();
                this.chartContainer.add(new JLabel("No data available for CFG analysis.", 0));
                this.chartContainer.revalidate();
                this.chartContainer.repaint();
                return;
            }
            Map<String, Double> ideal = Map.of("Vegetables and Fruits", 50.0, "Grain Products", 25.0, "Milk and Alternatives", 12.5, "Meat and Alternatives", 12.5);
            this.updateCharts(actual, ideal, day);
            this.statusLabel.setText("CFG analysis completed successfully.");
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Profile ID.", "Input Error", 0);
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", 0);
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading CFG data: " + ex.getMessage(), "Error", 0);
        }
    }

    private void updateCharts(Map<String, Double> map, Map<String, Double> map2, LocalDate localDate) {
        throw new Error("Unresolved compilation problems: \n\tDefaultPieDataset cannot be resolved to a type\n\tDefaultPieDataset cannot be resolved to a type\n\tJFreeChart cannot be resolved to a type\n\tChartFactory cannot be resolved\n\tPiePlot cannot be resolved to a type\n\tPiePlot cannot be resolved to a type\n\tThe method add(Component) in the type Container is not applicable for the arguments (ChartPanel)\n\tChartPanel cannot be resolved to a type\n\tDefaultPieDataset cannot be resolved to a type\n\tDefaultPieDataset cannot be resolved to a type\n\tJFreeChart cannot be resolved to a type\n\tChartFactory cannot be resolved\n\tPiePlot cannot be resolved to a type\n\tPiePlot cannot be resolved to a type\n\tThe method add(Component) in the type Container is not applicable for the arguments (ChartPanel)\n\tChartPanel cannot be resolved to a type\n");
    }

    public void setProfile(int profileId) {
        this.tfProfileId.setText(String.valueOf(profileId));
        this.loadCfg(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception exception) {
                // empty catch block
            }
            JFrame frame = new JFrame("CFG Compliance Panel - UC7 Test");
            frame.setDefaultCloseOperation(3);
            CfgCompliancePanel panel = new CfgCompliancePanel();
            frame.add(panel);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
