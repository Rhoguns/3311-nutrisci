/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  DailySummary
 */
package com.nutrisci.ui;

import com.nutrisci.dao.DAOFactory;
import com.nutrisci.service.AnalysisModule;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class DailyIntakePanel
extends JPanel {
    private final JTextField tfProfileId = new JTextField(8);
    private final JTextField tfFromDate = new JTextField(LocalDate.now().minusDays(7L).toString(), 12);
    private final JTextField tfToDate = new JTextField(LocalDate.now().toString(), 12);
    private final JButton btnLoad = new JButton("Load Data");
    private final JLabel statusLabel = new JLabel("Ready - Enter profile and date range to load data.");
    private final JTable intakeTable = new JTable();
    private final JPanel chartContainer = new JPanel(new GridLayout(1, 2, 10, 10));
    private final AnalysisModule analysisModule = new AnalysisModule(DAOFactory.getNutritionDAO());
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DailyIntakePanel() {
        this.initComponents();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        this.add((Component)this.createTopPanel(), "North");
        JSplitPane splitPane = new JSplitPane(0, new JScrollPane(this.intakeTable), this.chartContainer);
        splitPane.setDividerLocation(250);
        this.add((Component)splitPane, "Center");
        this.add((Component)this.createStatusPanel(), "South");
        this.chartContainer.add(new JLabel("Charts will appear here after loading data.", 0));
        this.btnLoad.addActionListener(this::loadDailyIntake);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(0, 10, 5));
        panel.add(new JLabel("Profile ID:"));
        panel.add(this.tfProfileId);
        panel.add(new JLabel("Start Date:"));
        panel.add(this.tfFromDate);
        panel.add(new JLabel("End Date:"));
        panel.add(this.tfToDate);
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

    private void loadDailyIntake(ActionEvent actionEvent) {
        throw new Error("Unresolved compilation problems: \n\tDailySummary cannot be resolved to a type\n\tThe method getDailyIntakeSummary(int, LocalDate, LocalDate) is undefined for the type AnalysisModule\n");
    }

    private void updateTable(List<DailySummary> list) {
        throw new Error("Unresolved compilation problem: \n\tDailySummary cannot be resolved to a type\n");
    }

    public void setProfile(int profileId) {
        this.tfProfileId.setText(String.valueOf(profileId));
        this.loadDailyIntake(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception exception) {
                // empty catch block
            }
            JFrame frame = new JFrame("Daily Intake Panel - UC6 Test");
            frame.setDefaultCloseOperation(3);
            DailyIntakePanel panel = new DailyIntakePanel();
            frame.add(panel);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
