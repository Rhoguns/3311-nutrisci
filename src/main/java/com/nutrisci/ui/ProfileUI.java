package com.nutrisci.ui;



import com.nutrisci.controller.ProfileController;
import com.nutrisci.dao.ProfileDAO;
import com.nutrisci.model.Profile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class ProfileUI extends JFrame {
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

    private void initComponents() {
        setTitle("Profile Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 2, 5, 5));

        add(new JLabel("Name:"));
        add(tfName);

        add(new JLabel("Sex:"));
        add(cbSex);

        add(new JLabel("Date of Birth:"));
        add(tfDob);

        add(new JLabel("Height (cm):"));
        add(tfHeight);

        add(new JLabel("Weight (kg):"));
        add(tfWeight);

        add(new JLabel("Unit:"));
        add(cbUnit);

        btnSave.addActionListener(new SaveListener());
        add(btnSave);

        btnLoad.addActionListener(new LoadListener());
        add(btnLoad);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class SaveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Profile p = new Profile();
                p.setName(tfName.getText().trim());
                p.setSex((String) cbSex.getSelectedItem());
                p.setDateOfBirth(LocalDate.parse(tfDob.getText().trim()));
                p.setHeightCm(Double.parseDouble(tfHeight.getText().trim()));
                p.setWeightKg(Double.parseDouble(tfWeight.getText().trim()));
                p.setUnit((String) cbUnit.getSelectedItem());

                controller.createProfile(p);
                JOptionPane.showMessageDialog(
                    ProfileUI.this,
                    "Profile saved with ID: " + p.getId(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (DateTimeParseException dtpe) {
                JOptionPane.showMessageDialog(
                    ProfileUI.this,
                    "Invalid date format. Use yyyy-MM-dd.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(
                    ProfileUI.this,
                    "Height and weight must be numbers.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    ProfileUI.this,
                    "Error saving profile:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private class LoadListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog(
                ProfileUI.this,
                "Enter Profile ID to load:",
                "Load Profile",
                JOptionPane.QUESTION_MESSAGE
            );
            if (input == null || input.isBlank()) return;
            try {
                int id = Integer.parseInt(input.trim());
                Optional<Profile> opt = controller.getProfileById(id);
                if (opt.isPresent()) {
                    Profile p = opt.get();
                    tfName.setText(p.getName());
                    cbSex.setSelectedItem(p.getSex());
                    tfDob.setText(p.getDateOfBirth().toString());
                    tfHeight.setText(String.valueOf(p.getHeightCm()));
                    tfWeight.setText(String.valueOf(p.getWeightKg()));
                    cbUnit.setSelectedItem(p.getUnit());
                } else {
                    JOptionPane.showMessageDialog(
                        ProfileUI.this,
                        "Profile not found.",
                        "Not Found",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(
                    ProfileUI.this,
                    "Invalid ID.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    ProfileUI.this,
                    "Error loading profile:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create a ProfileController with a new ProfileDAO to handle profile operations.
            ProfileController controller = new ProfileController(new ProfileDAO());
            // Create and display the ProfileUI, passing the controller to it.
            new ProfileUI(controller);
        });
    }
}