/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import com.nutrisci.model.Profile;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;
import javax.swing.JOptionPane;

private class ProfileUI.LoadListener
implements ActionListener {
    private ProfileUI.LoadListener() {
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
