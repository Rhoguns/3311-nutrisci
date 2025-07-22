/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import com.nutrisci.model.Exercise;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;

private class ExerciseLoggerUI.SaveListener
implements ActionListener {
    private ExerciseLoggerUI.SaveListener() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Exercise ex = new Exercise();
            ex.setProfileId(Integer.parseInt(ExerciseLoggerUI.this.tfProfileId.getText().trim()));
            ex.setName(ExerciseLoggerUI.this.tfName.getText().trim());
            ex.setDurationMinutes(Double.parseDouble(ExerciseLoggerUI.this.tfDuration.getText().trim()));
            ex.setCaloriesBurned(Double.parseDouble(ExerciseLoggerUI.this.tfCalories.getText().trim()));
            LocalDateTime dt = LocalDateTime.parse(ExerciseLoggerUI.this.tfPerformedAt.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            ex.setPerformedAt(dt);
            int id = ExerciseLoggerUI.this.dao.insert(ex);
            JOptionPane.showMessageDialog(ExerciseLoggerUI.this, "Saved exercise with ID: " + id, "Success", 1);
            ExerciseLoggerUI.this.clearFields();
        }
        catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(ExerciseLoggerUI.this, "Please enter valid numbers.", "Input Error", 0);
        }
        catch (SQLException sqle) {
            JOptionPane.showMessageDialog(ExerciseLoggerUI.this, "Database error:\n" + sqle.getMessage(), "DB Error", 0);
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(ExerciseLoggerUI.this, "Error:\n" + ex.getMessage(), "Error", 0);
        }
    }
}
