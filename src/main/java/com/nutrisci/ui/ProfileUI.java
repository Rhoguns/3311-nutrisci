package com.nutrisci.ui;

import com.nutrisci.controller.ProfileController;
import com.nutrisci.dao.ProfileDAO;
import com.nutrisci.dao.ProfileDAOImpl;
import com.nutrisci.model.Profile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class ProfileUI extends JPanel {
    private Profile currentProfile; // This field should already exist
    private int profileId = 1; // Default profile

    private final JTextField tfName       = new JTextField();
    private final JComboBox<String> cbSex = new JComboBox<>(new String[]{"M", "F", "Other"});
    private final JTextField tfDob        = new JTextField("yyyy-MM-dd");
    private final JTextField tfHeight     = new JTextField();
    private final JTextField tfWeight     = new JTextField();
    private final JComboBox<String> cbUnit = new JComboBox<>(new String[]{"metric", "imperial"});
    private final JButton btnSave         = new JButton("Save");
    private final JButton btnLoad         = new JButton("Load");

    private final ProfileController controller;

    public ProfileUI(ProfileController controller) {
        this.controller = controller;
        initComponents();
    }

    public void setCurrentProfile(Profile profile) {
        this.currentProfile = profile;
        // Update UI fields to reflect the loaded profile's data
        tfName.setText(profile.getName());
        cbSex.setSelectedItem(profile.getSex());
        tfDob.setText(profile.getDateOfBirth().toString());
        tfHeight.setText(String.valueOf(profile.getHeightCm()));
        tfWeight.setText(String.valueOf(profile.getWeightKg()));
        cbUnit.setSelectedItem(profile.getUnit());
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("UC4: Profile Management"));
        
        JLabel titleLabel = new JLabel("Profile Management Interface", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Current Profile ID display
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Current Profile ID:"), gbc);
        gbc.gridx = 1;
        JLabel profileLabel = new JLabel(String.valueOf(profileId));
        controlPanel.add(profileLabel, gbc);
        
        // Profile Name
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("Profile Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField("User " + profileId, 20);
        controlPanel.add(nameField, gbc);
        
        // Age
        gbc.gridx = 0; gbc.gridy = 2;
        controlPanel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1;
        JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(25, 1, 120, 1));
        controlPanel.add(ageSpinner, gbc);
        
        // Weight
        gbc.gridx = 0; gbc.gridy = 3;
        controlPanel.add(new JLabel("Weight (kg):"), gbc);
        gbc.gridx = 1;
        JSpinner weightSpinner = new JSpinner(new SpinnerNumberModel(70.0, 30.0, 300.0, 0.1));
        controlPanel.add(weightSpinner, gbc);
        
        // Height
        gbc.gridx = 0; gbc.gridy = 4;
        controlPanel.add(new JLabel("Height (cm):"), gbc);
        gbc.gridx = 1;
        JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(175, 100, 250, 1));
        controlPanel.add(heightSpinner, gbc);
        
        // Update button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JButton updateButton = new JButton("Update Profile");
        updateButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Profile updated successfully!\n" +
                "Profile ID: " + profileId + "\n" +
                "Name: " + nameField.getText() + "\n" +
                "Age: " + ageSpinner.getValue() + "\n" +
                "Weight: " + weightSpinner.getValue() + " kg\n" +
                "Height: " + heightSpinner.getValue() + " cm", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        controlPanel.add(updateButton, gbc);
        
        add(controlPanel, BorderLayout.CENTER);
        
        // Status area
        JTextArea statusArea = new JTextArea(3, 50);
        statusArea.setEditable(false);
        statusArea.setText("Managing profile for Profile ID: " + profileId);
        statusArea.setBackground(getBackground());
        add(statusArea, BorderLayout.SOUTH);
    }

    public void setProfile(int profileId) {
        this.profileId = profileId;
        updateProfileInComponents(this, profileId);
        // Update status area
        updateStatusArea();
    }

    private void updateStatusArea() {
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JTextArea) {
                JTextArea statusArea = (JTextArea) comp;
                if (statusArea.getText().contains("Managing profile")) {
                    statusArea.setText("Managing profile for Profile ID: " + profileId);
                }
            }
        }
    }

    private void updateProfileInComponents(Container container, int newProfileId) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getText().matches("\\d+")) {
                    label.setText(String.valueOf(newProfileId));
                }
            } else if (comp instanceof JTextField) {
                JTextField field = (JTextField) comp;
                if (field.getText().startsWith("User ")) {
                    field.setText("User " + newProfileId);
                }
            } else if (comp instanceof Container) {
                updateProfileInComponents((Container) comp, newProfileId);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Profile UI Test");
            // FIX: Correctly instantiate the controller with the implementation class (ProfileDAOImpl)
            // and remove the duplicate variable declaration.
            ProfileController controller = new ProfileController(new ProfileDAOImpl());
            ProfileUI profileUI = new ProfileUI(controller);
            profileUI.setProfile(1);
            frame.add(profileUI);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}