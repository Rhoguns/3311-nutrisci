package com.nutrisci.ui;

import com.nutrisci.dao.DAOFactory;
import com.nutrisci.dao.MealDAO;
import com.nutrisci.dao.SwapRuleDAO;
import com.nutrisci.logic.SwapEngine;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class BatchSwapPanel
extends JPanel {
    private JTextField profileIdField, swapRuleIdField;
    private JSpinner startDateSpinner, endDateSpinner;
    private SwapEngine swapEngine;

    public BatchSwapPanel() {
        SwapRuleDAO swapRuleDAO = DAOFactory.getSwapRuleDAO();
        MealDAO mealDAO = DAOFactory.getMealDAO();
        this.swapEngine = new SwapEngine(swapRuleDAO, mealDAO);
        this.initializeUI();
    }

    private void initializeUI() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createTitledBorder("Apply Swap Rule Over Time"));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        
        this.profileIdField = new JTextField("1", 10);
        this.swapRuleIdField = new JTextField(10);

        // Default dates for demonstration
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JULY, 1);
        Date startDate = cal.getTime();
        cal.set(2025, Calendar.JULY, 19);
        Date endDate = cal.getTime();

        this.startDateSpinner = new JSpinner(new SpinnerDateModel(startDate, null, null, Calendar.DAY_OF_MONTH));
        this.startDateSpinner.setEditor(new JSpinner.DateEditor(this.startDateSpinner, "yyyy-MM-dd"));
        this.endDateSpinner = new JSpinner(new SpinnerDateModel(endDate, null, null, Calendar.DAY_OF_MONTH));
        this.endDateSpinner.setEditor(new JSpinner.DateEditor(this.endDateSpinner, "yyyy-MM-dd"));

        formPanel.add(new JLabel("Profile ID:"));
        formPanel.add(this.profileIdField);
        formPanel.add(new JLabel("Swap Rule ID:"));
        formPanel.add(this.swapRuleIdField);
        formPanel.add(new JLabel("Start Date:"));
        formPanel.add(this.startDateSpinner);
        formPanel.add(new JLabel("End Date:"));
        formPanel.add(this.endDateSpinner);

        JButton applyButton = new JButton("Apply Over Time");
        applyButton.addActionListener(e -> this.applySwapOverTime());

        this.add(formPanel, BorderLayout.CENTER);
        this.add(applyButton, BorderLayout.SOUTH);
    }

    private void applySwapOverTime() {
        try {
            int profileId = Integer.parseInt(this.profileIdField.getText());
            int swapRuleId = Integer.parseInt(this.swapRuleIdField.getText());
            LocalDate startDate = ((Date) this.startDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = ((Date) this.endDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            int updatedCount = this.swapEngine.applySwapRuleOverTime(profileId, swapRuleId, startDate, endDate);

            JOptionPane.showMessageDialog(this,
                "Successfully applied swap to " + updatedCount + " meals.",
                "Batch Swap Complete",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for IDs.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Batch Swap Tool");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new BatchSwapPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
