package com.nutrisci.ui;

import javax.swing.*;
import java.awt.*;

public class ExerciseLoggerUI extends JPanel {
    private int profileId = 1; // Default profile

    public ExerciseLoggerUI() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("UC3: Exercise Logger"));
        
        JLabel titleLabel = new JLabel("Exercise Logging Interface", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Profile ID display
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Profile ID:"), gbc);
        gbc.gridx = 1;
        JLabel profileLabel = new JLabel(String.valueOf(profileId));
        controlPanel.add(profileLabel, gbc);
        
        // Exercise type
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("Exercise Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> exerciseCombo = new JComboBox<>(new String[]{"Running", "Walking", "Cycling", "Swimming", "Weight Training", "Yoga"});
        controlPanel.add(exerciseCombo, gbc);
        
        // Duration
        gbc.gridx = 0; gbc.gridy = 2;
        controlPanel.add(new JLabel("Duration (minutes):"), gbc);
        gbc.gridx = 1;
        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 300, 1));
        controlPanel.add(durationSpinner, gbc);
        
        // Intensity
        gbc.gridx = 0; gbc.gridy = 3;
        controlPanel.add(new JLabel("Intensity:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> intensityCombo = new JComboBox<>(new String[]{"Low", "Moderate", "High"});
        controlPanel.add(intensityCombo, gbc);
        
        // Log button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton logButton = new JButton("Log Exercise");
        logButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Exercise logged successfully!\n" +
                "Profile: " + profileId + "\n" +
                "Exercise: " + exerciseCombo.getSelectedItem() + "\n" +
                "Duration: " + durationSpinner.getValue() + " minutes\n" +
                "Intensity: " + intensityCombo.getSelectedItem(), 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        controlPanel.add(logButton, gbc);
        
        add(controlPanel, BorderLayout.CENTER);
        
        // Status area
        JTextArea statusArea = new JTextArea(3, 50);
        statusArea.setEditable(false);
        statusArea.setText("Ready to log exercises for Profile " + profileId);
        statusArea.setBackground(getBackground());
        add(statusArea, BorderLayout.SOUTH);
    }

    public void setProfile(int profileId) {
        this.profileId = profileId;
        updateProfileInComponents(this, profileId);
    }

    private void updateProfileInComponents(Container container, int newProfileId) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getText().matches("\\d+")) {
                    label.setText(String.valueOf(newProfileId));
                }
            } else if (comp instanceof Container) {
                updateProfileInComponents((Container) comp, newProfileId);
            }
        }
    }
}