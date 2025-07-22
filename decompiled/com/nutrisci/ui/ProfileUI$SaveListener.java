/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import com.nutrisci.model.Profile;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.JOptionPane;

private class ProfileUI.SaveListener
implements ActionListener {
    private ProfileUI.SaveListener() {
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
