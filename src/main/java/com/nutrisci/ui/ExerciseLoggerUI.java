package com.nutrisci.ui;

import com.nutrisci.dao.ExerciseDAO;
import com.nutrisci.dao.MySQLExerciseDAO;
import com.nutrisci.model.Exercise;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExerciseLoggerUI extends JFrame {
    private final JTextField tfProfileId   = new JTextField();
    private final JTextField tfName        = new JTextField();
    private final JTextField tfDuration    = new JTextField();
    // Text field for calories burned during the exercise.
    private final JTextField tfCalories    = new JTextField();
    // Text field for the timestamp when the exercise was performed.
    private final JTextField tfPerformedAt = new JTextField();    
    // Button to save the exercise record.
    private final JButton    btnSave       = new JButton("Save");
    // Button to clear all input fields.
    private final JButton    btnClear      = new JButton("Clear");

    /**
     * Data Access Object for Exercise operations.
     * This field is final and initialized via the constructor.
     */
    private final ExerciseDAO dao;

    public ExerciseLoggerUI(ExerciseDAO dao) {
        this.dao = dao;
        initComponents();
    }

    private void initComponents() {
        setTitle("Exercise Logger");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 2, 5, 5));

        add(new JLabel("Profile ID:"));
        add(tfProfileId);

        add(new JLabel("Exercise Name:"));
        add(tfName);

        add(new JLabel("Duration (min):"));
        add(tfDuration);

        add(new JLabel("Calories Burned:"));
        add(tfCalories);

        add(new JLabel("Performed At (yyyy-MM-dd HH:mm):"));
        tfPerformedAt.setText(LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        add(tfPerformedAt);

        btnSave.addActionListener(new SaveListener());
        add(btnSave);

        btnClear.addActionListener(e -> clearFields());
        add(btnClear);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void clearFields() {
        tfProfileId.setText("");
        tfName.setText("");
        tfDuration.setText("");
        tfCalories.setText("");
        tfPerformedAt.setText("");
    }

    private class SaveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Exercise ex = new Exercise();
                ex.setProfileId(Integer.parseInt(tfProfileId.getText().trim()));
                ex.setName(tfName.getText().trim());
                ex.setDurationMinutes(Double.parseDouble(tfDuration.getText().trim()));
                ex.setCaloriesBurned(Double.parseDouble(tfCalories.getText().trim()));
                LocalDateTime dt = LocalDateTime.parse(
                    tfPerformedAt.getText().trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                );
                ex.setPerformedAt(dt);

                int id = dao.insert(ex);
                JOptionPane.showMessageDialog(
                    ExerciseLoggerUI.this,
                    "Saved exercise with ID: " + id,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                clearFields();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(
                    ExerciseLoggerUI.this,
                    "Please enter valid numbers.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(
                    ExerciseLoggerUI.this,
                    "Database error:\n" + sqle.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    ExerciseLoggerUI.this,
                    "Error:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExerciseDAO dao = new MySQLExerciseDAO(); 
            new ExerciseLoggerUI(dao);
        });
    }
}