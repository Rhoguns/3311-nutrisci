package com.nutrisci.ui;

import com.nutrisci.controller.ProfileController;
import com.nutrisci.dao.ProfileDAOImpl;
import com.nutrisci.model.Profile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class ProfileUI extends JPanel {
    private Profile currentProfile;

    private JTextField tfName = new JTextField(20);
    private JComboBox<String> cbSex = new JComboBox<>(new String[]{"M", "F", "Other"});
    private JTextField tfDob = new JTextField("", 15);
    private JTextField tfHeight = new JTextField("", 10);
    private JTextField tfWeight = new JTextField("", 10);
    private JButton btnSave = new JButton("Save Profile");
    private JButton btnLoad = new JButton("Load Profile");
    private JButton btnNew = new JButton("New Profile");
    private JLabel statusLabel = new JLabel("Ready");

    private ProfileController controller;
    private boolean isNewProfile = false;

    public ProfileUI(ProfileController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Create/Edit Profile"));
        
        // Title
        JLabel titleLabel = new JLabel("Profile Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);
        
        // Main form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Profile Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfName, gbc);
        
        // Sex
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Sex:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbSex, gbc);
        
        // Date of Birth
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Date of Birth (yyyy-mm-dd):"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfDob, gbc);
        
        // Height
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Height (cm):"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfHeight, gbc);
        
        // Weight
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Weight (kg):"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfWeight, gbc);
        add(formPanel, BorderLayout.CENTER);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout());
        statusPanel.add(statusLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        // Save button
        btnSave.addActionListener(this::onSaveProfile);
        buttonPanel.add(btnSave);
        
        // Load button
        btnLoad.addActionListener(this::onLoadProfile);
        buttonPanel.add(btnLoad);
        buttonPanel.add(btnNew);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(statusPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(southPanel, BorderLayout.SOUTH);
        // New Profile button
        btnNew.addActionListener(this::onNewProfile);
        buttonPanel.add(btnNew);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void onSaveProfile(ActionEvent e) {
        try {
            Profile profile = new Profile();
            profile.setName(tfName.getText().trim());
            profile.setSex((String) cbSex.getSelectedItem());
            profile.setDateOfBirth(LocalDate.parse(tfDob.getText().trim()));
            profile.setHeightCm(Double.parseDouble(tfHeight.getText().trim()));
            profile.setWeightKg(Double.parseDouble(tfWeight.getText().trim()));
            
            // Set default values for removed fields
            profile.setUnit("metric");  // Default to metric
            profile.setEmail(null);     // No email field

            Profile savedProfile;
            if (isNewProfile || currentProfile == null) {
                savedProfile = controller.createProfile(profile);
                isNewProfile = false;
                currentProfile = savedProfile;
                
                // Expected popup: "Profile saved with ID: X"
                JOptionPane.showMessageDialog(this, 
                    "Profile saved with ID: " + savedProfile.getId(), 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
                // Update the profile ID for other components
                setProfile(savedProfile.getId());
                
            } else {
                // Update existing profile
                profile.setId(currentProfile.getId());
                controller.updateProfile(profile);
                currentProfile = profile;
                
                JOptionPane.showMessageDialog(this, 
                    "Profile updated successfully!\nProfile ID: " + profile.getId(), 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, 
                "Invalid date format. Please use yyyy-mm-dd", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Invalid number format for height or weight", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error saving profile: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void onLoadProfile(ActionEvent e) {
        String input = JOptionPane.showInputDialog(this, "Enter Profile ID to load:");
        if (input != null && !input.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(input.trim());
                Optional<Profile> profileOpt = controller.getProfile(id);
                if (profileOpt.isPresent()) {
                    setCurrentProfile(profileOpt.get());
                    isNewProfile = false;
                    JOptionPane.showMessageDialog(this, "Profile loaded successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Profile not found with ID: " + id, "Not Found", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Profile ID", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading profile: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onNewProfile(ActionEvent e) {
        // Clear form for new profile - REMOVED default "TestUser" data
        tfName.setText("");
        cbSex.setSelectedItem("M");
        tfDob.setText("");
        tfHeight.setText("");
        tfWeight.setText("");
        
        currentProfile = null;
        isNewProfile = true;
        
        // Popup message removed - just update status
        statusLabel.setText("Ready to create new profile. Fill in details and click Save.");
    }

    public void setCurrentProfile(Profile profile) {
        this.currentProfile = profile;
        isNewProfile = false;
        
        // Update UI fields to reflect the loaded profile's data
        tfName.setText(profile.getName());
        cbSex.setSelectedItem(profile.getSex());
        tfDob.setText(profile.getDateOfBirth().toString());
        tfHeight.setText(String.valueOf(profile.getHeightCm()));
        tfWeight.setText(String.valueOf(profile.getWeightKg()));
        // Unit and email removed from UI
    }

    public void setProfile(int profileId) {
        statusLabel.setText("Profile set to: " + profileId);
        
        try {
            Optional<Profile> profile = controller.getProfile(profileId);
            if (profile.isPresent()) {
                setCurrentProfile(profile.get());
                statusLabel.setText("Loaded profile: " + profile.get().getName());
            } else {
                statusLabel.setText("Profile " + profileId + " not found. Create a new profile.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading profile: " + e.getMessage());
        }
    }

    // Method used by MainDashboard to set profile ID from other components
    public void updateProfileId(int newProfileId) {
        setProfile(newProfileId);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Profile UI Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            ProfileController controller = new ProfileController(new ProfileDAOImpl());
            ProfileUI profileUI = new ProfileUI(controller);
            
            frame.add(profileUI);
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}