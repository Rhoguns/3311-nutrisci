/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import com.nutrisci.controller.ProfileController;
import com.nutrisci.dao.ProfileDAOImpl;

import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {
    private final int profileId;
    private JTabbedPane mainTabbedPane;
    private JLabel profileLabel;

    public MainDashboard(int profileId) {
        this.profileId = profileId;
        initializeUI();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("NutriSci Dashboard - Profile " + profileId);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        
        setLayout(new BorderLayout());
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        mainTabbedPane = new JTabbedPane();
        addAllTabs();
        add(mainTabbedPane, BorderLayout.CENTER);
        
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setBackground(new Color(240, 248, 255));
        
        JLabel titleLabel = new JLabel("NutriSci Nutrition Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112));
        
        profileLabel = new JLabel("Active Profile: " + profileId, SwingConstants.RIGHT);
        profileLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        profileLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));
        
        JButton changeProfileBtn = new JButton("Change Profile");
        changeProfileBtn.addActionListener(e -> changeProfile());
        
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        profilePanel.setOpaque(false);
        profilePanel.add(profileLabel);
        profilePanel.add(changeProfileBtn);
        
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(profilePanel, BorderLayout.EAST);
        
        return panel;
    }

    private void addAllTabs() {
        try {
            // UC1 & UC6: Daily Intake Visualization
            DailyIntakePanel dailyIntakePanel = new DailyIntakePanel();
            dailyIntakePanel.setProfile(profileId);
            mainTabbedPane.addTab("UC1/UC6: Daily Intake", dailyIntakePanel);
            
            // UC2: Meal Logging
            MealLoggerUI mealLoggerUI = new MealLoggerUI();
            if (hasSetProfileMethod(mealLoggerUI)) {
                mealLoggerUI.setProfile(profileId);
            }
            mainTabbedPane.addTab("UC2: Meal Logger", mealLoggerUI);
            
            // UC3: Exercise Logging - Use no-arg constructor as per documentation
            ExerciseLoggerUI exerciseLoggerUI = new ExerciseLoggerUI();
            if (hasSetProfileMethod(exerciseLoggerUI)) {
                exerciseLoggerUI.setProfile(profileId);
            }
            mainTabbedPane.addTab("UC3: Exercise Logger", exerciseLoggerUI);
            
            // UC4: Profile Management - Create with proper controller
            ProfileController profileController = new ProfileController(new ProfileDAOImpl());
            ProfileUI profileUI = new ProfileUI(profileController);
            if (hasSetProfileMethod(profileUI)) {
                profileUI.setProfile(profileId);
            }
            mainTabbedPane.addTab("UC4: Profile Manager", profileUI);
            
            // UC5 & UC8: Swap Impact Analysis
            SwapImpactPanel swapImpactPanel = new SwapImpactPanel();
            swapImpactPanel.setProfile(profileId);
            mainTabbedPane.addTab("UC5/UC8: Swap Analysis", swapImpactPanel);
            
            // UC7: CFG Compliance
            CfgCompliancePanel cfgCompliancePanel = new CfgCompliancePanel();
            cfgCompliancePanel.setProfile(profileId);
            mainTabbedPane.addTab("UC7: CFG Compliance", cfgCompliancePanel);
            
        } catch (Exception e) {
            JPanel errorPanel = new JPanel(new BorderLayout());
            JTextArea errorText = new JTextArea("Error loading UI panels:\n" + e.getMessage());
            errorText.setEditable(false);
            errorText.setBackground(Color.LIGHT_GRAY);
            errorPanel.add(new JScrollPane(errorText), BorderLayout.CENTER);
            mainTabbedPane.addTab("Error", errorPanel);
            e.printStackTrace();
        }
    }

    private boolean hasSetProfileMethod(Object obj) {
        try {
            obj.getClass().getMethod("setProfile", int.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setBackground(new Color(245, 245, 245));
        
        JLabel statusLabel = new JLabel("Ready - All nutrition tracking features available", SwingConstants.LEFT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        
        JLabel versionLabel = new JLabel("NutriSci v1.0 | EECS 3311", SwingConstants.RIGHT);
        versionLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));
        versionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        
        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(versionLabel, BorderLayout.EAST);
        
        return panel;
    }

    private void changeProfile() {
        String input = JOptionPane.showInputDialog(this, "Enter new Profile ID:", "Change Profile", JOptionPane.QUESTION_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                int newProfileId = Integer.parseInt(input.trim());
                if (newProfileId > 0) {
                    dispose();
                    new MainDashboard(newProfileId);
                } else {
                    JOptionPane.showMessageDialog(this, "Profile ID must be a positive number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric Profile ID.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            if (args.length > 0) {
                try {
                    int profileId = Integer.parseInt(args[0]);
                    new MainDashboard(profileId);
                } catch (NumberFormatException e) {
                    new MainDashboard(1); // Default profile
                }
            } else {
                new MainDashboard(1); // Default profile
            }
        });
    }
}
