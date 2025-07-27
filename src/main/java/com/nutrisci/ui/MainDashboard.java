package com.nutrisci.ui;

import com.nutrisci.controller.ProfileController;
import com.nutrisci.dao.ProfileDAOImpl;

import javax.swing.*;
import java.awt.*;

/**
 * Main app window.
 */
public class MainDashboard extends JFrame {
    private int profileId;
    private JTabbedPane mainTabbedPane;
    private JLabel profileLabel;

    public MainDashboard(int profileId) {
        if (profileId <= 0) {
            throw new IllegalArgumentException("Profile ID must be positive");
        }
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
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("NutriSci Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.CENTER);
        
        // Profile management panel
        JPanel profilePanel = new JPanel(new FlowLayout());
        profileLabel = new JLabel("Profile: " + profileId);
        profileLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JButton changeProfileBtn = new JButton("Change Profile");
        changeProfileBtn.addActionListener(e -> changeProfile());
        
        profilePanel.add(profileLabel);
        profilePanel.add(changeProfileBtn);
        panel.add(profilePanel, BorderLayout.EAST);
        
        return panel;
    }

    private void addAllTabs() {
        try {
            // Profile Management - First tab
            ProfileController profileController = new ProfileController(new ProfileDAOImpl());
            ProfileUI profileUI = new ProfileUI(profileController);
            mainTabbedPane.addTab("Profile Manager", profileUI);
            profileUI.revalidate();
            if (hasSetProfileMethod(profileUI)) {
                profileUI.setProfile(profileId);
            }
            
            // Meal Logging - Second tab
            MealLoggerUI mealLoggerUI = new MealLoggerUI();
            mainTabbedPane.addTab("Meal Logger", mealLoggerUI);
            mealLoggerUI.revalidate();
            if (hasSetProfileMethod(mealLoggerUI)) {
                mealLoggerUI.setProfile(profileId);
            }

            // Meal Journal - Third tab
            MealJournalUI mealJournalUI = new MealJournalUI();
            mainTabbedPane.addTab("Meal Journal", mealJournalUI);
            mealJournalUI.revalidate();
            if (hasSetProfileMethod(mealJournalUI)) {
                mealJournalUI.setProfile(profileId);
            }
            
            // Compare Meals - Fourth tab
            CompareMealsPanel compareMealsPanel = new CompareMealsPanel();
            mainTabbedPane.addTab("Compare Meals", compareMealsPanel);
            compareMealsPanel.revalidate();
            if (hasSetProfileMethod(compareMealsPanel)) {
                compareMealsPanel.setProfile(profileId);
            }
            
            // Swap Analysis - Fifth tab
            SwapImpactPanel swapAnalysisPanel = new SwapImpactPanel();
            mainTabbedPane.addTab("Swap Analysis", swapAnalysisPanel);
            swapAnalysisPanel.revalidate();
            if (hasSetProfileMethod(swapAnalysisPanel)) {
                swapAnalysisPanel.setProfile(profileId);
            }

            // Daily Intake Analysis - Sixth tab
            DailyIntakePanel dailyIntakePanel = new DailyIntakePanel();
            mainTabbedPane.addTab("Daily Intake Analysis", dailyIntakePanel);
            dailyIntakePanel.revalidate();
            dailyIntakePanel.setProfile(profileId);
            
            // CFG Compliance - Seventh tab
            CfgCompliancePanel cfgCompliancePanel = new CfgCompliancePanel();
            mainTabbedPane.addTab("CFG Compliance", cfgCompliancePanel);
            cfgCompliancePanel.revalidate();
            cfgCompliancePanel.setProfile(profileId);
            
            // Exercise Logger - Last tab
            ExerciseLoggerUI exerciseLoggerUI = new ExerciseLoggerUI();
            mainTabbedPane.addTab("Exercise Logger", exerciseLoggerUI);
            exerciseLoggerUI.revalidate();
            if (hasSetProfileMethod(exerciseLoggerUI)) {
                exerciseLoggerUI.setProfile(profileId);
            }
            
        } catch (Exception e) {
            System.err.println("Error in addAllTabs: " + e.getMessage());
            
            JPanel errorPanel = new JPanel(new BorderLayout());
            JTextArea errorText = new JTextArea("Error loading UI panels:\n" + e.getMessage() + 
                "\n\nStack trace:\n" + getStackTrace(e));
            errorText.setEditable(false);
            errorText.setBackground(Color.LIGHT_GRAY);
            errorPanel.add(new JScrollPane(errorText), BorderLayout.CENTER);
            mainTabbedPane.addTab("Error", errorPanel);
        }
    }
    
    private String getStackTrace(Exception e) {
        return e.getMessage();
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
        String input = JOptionPane.showInputDialog(this, 
            "Enter new Profile ID:", 
            "Change Profile", 
            JOptionPane.QUESTION_MESSAGE);
            
        if (input != null && !input.trim().isEmpty()) {
            try {
                int newProfileId = Integer.parseInt(input.trim());
                if (newProfileId > 0) {
                    // Update all components with new profile ID
                    updateAllComponentsProfileId(newProfileId);
                    this.profileId = newProfileId;
                    profileLabel.setText("Profile: " + profileId);
                    setTitle("NutriSci Dashboard - Profile " + profileId);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Profile ID must be a positive number.", 
                        "Invalid Input", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid numeric Profile ID.", 
                    "Invalid Input", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateAllComponentsProfileId(int newProfileId) {
        for (int i = 0; i < mainTabbedPane.getTabCount(); i++) {
            Component tabComponent = mainTabbedPane.getComponentAt(i);
            if (hasSetProfileMethod(tabComponent)) {
                try {
                    tabComponent.getClass().getMethod("setProfile", int.class)
                        .invoke(tabComponent, newProfileId);
                } catch (Exception e) {
                    System.err.println("Error updating profile for tab " + i + ": " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception err) {
            // Use default look and feel
        }
        
        SwingUtilities.invokeLater(() -> {
            // No default profile - must be provided
            if (args.length > 0) {
                try {
                    int profileId = Integer.parseInt(args[0]);
                    new MainDashboard(profileId);
                } catch (NumberFormatException e) {
                    showProfileSelector();
                }
            } else {
                showProfileSelector();
            }
        });
    }

    private static void showProfileSelector() {
        // Launch ProfileSelector if no valid profile ID provided
        new ProfileSelector();
    }
}
