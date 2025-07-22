/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import com.nutrisci.dao.ExerciseDAO;
import com.nutrisci.dao.MySQLExerciseDAO;
import com.nutrisci.model.Exercise;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ExerciseLoggerUI
extends JFrame {
    private final JTextField tfProfileId = new JTextField();
    private final JTextField tfName = new JTextField();
    private final JTextField tfDuration = new JTextField();
    private final JTextField tfCalories = new JTextField();
    private final JTextField tfPerformedAt = new JTextField();
    private final JButton btnSave = new JButton("Save");
    private final JButton btnClear = new JButton("Clear");
    private final ExerciseDAO dao;

    public ExerciseLoggerUI(ExerciseDAO dao) {
        this.dao = dao;
        this.initComponents();
    }

    private void initComponents() {
        this.setTitle("Exercise Logger");
        this.setDefaultCloseOperation(3);
        this.setLayout(new GridLayout(6, 2, 5, 5));
        this.add(new JLabel("Profile ID:"));
        this.add(this.tfProfileId);
        this.add(new JLabel("Exercise Name:"));
        this.add(this.tfName);
        this.add(new JLabel("Duration (min):"));
        this.add(this.tfDuration);
        this.add(new JLabel("Calories Burned:"));
        this.add(this.tfCalories);
        this.add(new JLabel("Performed At (yyyy-MM-dd HH:mm):"));
        this.tfPerformedAt.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        this.add(this.tfPerformedAt);
        this.btnSave.addActionListener(new SaveListener());
        this.add(this.btnSave);
        this.btnClear.addActionListener(e -> this.clearFields());
        this.add(this.btnClear);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void clearFields() {
        this.tfProfileId.setText("");
        this.tfName.setText("");
        this.tfDuration.setText("");
        this.tfCalories.setText("");
        this.tfPerformedAt.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MySQLExerciseDAO dao = new MySQLExerciseDAO();
            new ExerciseLoggerUI(dao);
        });
    }

    private class SaveListener
    implements ActionListener {
        private SaveListener() {
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
}
