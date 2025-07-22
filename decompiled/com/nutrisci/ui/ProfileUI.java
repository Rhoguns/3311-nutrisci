/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import com.nutrisci.controller.ProfileController;
import com.nutrisci.dao.DAOFactory;
import com.nutrisci.model.Profile;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ProfileUI
extends JFrame {
    private final JTextField tfName = new JTextField();
    private final JComboBox<String> cbSex = new JComboBox<String>(new String[]{"M", "F", "Other"});
    private final JTextField tfDob = new JTextField("yyyy-MM-dd");
    private final JTextField tfHeight = new JTextField();
    private final JTextField tfWeight = new JTextField();
    private final JComboBox<String> cbUnit = new JComboBox<String>(new String[]{"metric", "imperial"});
    private final JButton btnSave = new JButton("Save");
    private final JButton btnLoad = new JButton("Load");
    private final ProfileController controller;

    public ProfileUI(ProfileController controller) {
        this.controller = controller;
        this.initComponents();
    }

    private void initComponents() {
        this.setTitle("Profile Manager");
        this.setDefaultCloseOperation(3);
        this.setLayout(new GridLayout(7, 2, 5, 5));
        this.add(new JLabel("Name:"));
        this.add(this.tfName);
        this.add(new JLabel("Sex:"));
        this.add(this.cbSex);
        this.add(new JLabel("Date of Birth:"));
        this.add(this.tfDob);
        this.add(new JLabel("Height (cm):"));
        this.add(this.tfHeight);
        this.add(new JLabel("Weight (kg):"));
        this.add(this.tfWeight);
        this.add(new JLabel("Unit:"));
        this.add(this.cbUnit);
        this.btnSave.addActionListener(new SaveListener());
        this.add(this.btnSave);
        this.btnLoad.addActionListener(new LoadListener());
        this.add(this.btnLoad);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProfileController controller = new ProfileController(DAOFactory.getProfileDAO());
            new ProfileUI(controller);
        });
    }

    private class LoadListener
    implements ActionListener {
        private LoadListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog(ProfileUI.this, "Enter Profile ID to load:", "Load Profile", 3);
            if (input == null || input.isBlank()) {
                return;
            }
            try {
                int id = Integer.parseInt(input.trim());
                Optional<Profile> opt = ProfileUI.this.controller.getProfileById(id);
                if (opt.isPresent()) {
                    Profile p = opt.get();
                    ProfileUI.this.tfName.setText(p.getName());
                    ProfileUI.this.cbSex.setSelectedItem(p.getSex());
                    ProfileUI.this.tfDob.setText(p.getDateOfBirth().toString());
                    ProfileUI.this.tfHeight.setText(String.valueOf(p.getHeightCm()));
                    ProfileUI.this.tfWeight.setText(String.valueOf(p.getWeightKg()));
                    ProfileUI.this.cbUnit.setSelectedItem(p.getUnit());
                } else {
                    JOptionPane.showMessageDialog(ProfileUI.this, "Profile not found.", "Not Found", 2);
                }
            }
            catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(ProfileUI.this, "Invalid ID.", "Input Error", 0);
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(ProfileUI.this, "Error loading profile:\n" + ex.getMessage(), "Error", 0);
            }
        }
    }

    private class SaveListener
    implements ActionListener {
        private SaveListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Profile p = new Profile();
                p.setName(ProfileUI.this.tfName.getText().trim());
                p.setSex((String)ProfileUI.this.cbSex.getSelectedItem());
                p.setDateOfBirth(LocalDate.parse(ProfileUI.this.tfDob.getText().trim()));
                p.setHeightCm(Double.parseDouble(ProfileUI.this.tfHeight.getText().trim()));
                p.setWeightKg(Double.parseDouble(ProfileUI.this.tfWeight.getText().trim()));
                p.setUnit((String)ProfileUI.this.cbUnit.getSelectedItem());
                ProfileUI.this.controller.createProfile(p);
                JOptionPane.showMessageDialog(ProfileUI.this, "Profile saved with ID: " + p.getId(), "Success", 1);
            }
            catch (DateTimeParseException dtpe) {
                JOptionPane.showMessageDialog(ProfileUI.this, "Invalid date format. Use yyyy-MM-dd.", "Input Error", 0);
            }
            catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(ProfileUI.this, "Height and weight must be numbers.", "Input Error", 0);
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(ProfileUI.this, "Error saving profile:\n" + ex.getMessage(), "Error", 0);
            }
        }
    }
}
