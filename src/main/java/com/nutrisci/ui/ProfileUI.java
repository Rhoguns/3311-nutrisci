package main.java.com.nutrisci.ui;


import main.java.com.nutrisci.model.Profile;
import main.java.com.nutrisci.dao.NutritionDAO;
import main.java.com.nutrisci.dao.InMemoryNutritionDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * A Swing-based UI for creating or editing a user Profile.
 * <p>
 * Presents fields for name, sex, age, height, and weight,
 * then displays the resulting Profile’s details (including BMI).
 */
public class ProfileUI extends JFrame {
    /** Text field for the user’s name. */
    private JTextField nameField;
    /** Text field for the user’s sex. */
    private JTextField sexField;
    /** Text field for the user’s age. */
    private JTextField ageField;
    /** Text field for the user’s height (cm). */
    private JTextField heightField;
    /** Text field for the user’s weight (kg). */
    private JTextField weightField;
    /** Button to save the profile. */
    private JButton saveBtn;

    /**
     * Constructs the Profile creation UI.
     * Sets up labels, text fields, and the Save button.
     */
    public ProfileUI() {
        super("Create Profile");
        setLayout(new GridLayout(6, 2));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Sex:"));
        sexField = new JTextField();
        add(sexField);

        add(new JLabel("Age:"));
        ageField = new JTextField();
        add(ageField);

        add(new JLabel("Height (cm):"));
        heightField = new JTextField();
        add(heightField);

        add(new JLabel("Weight (kg):"));
        weightField = new JTextField();
        add(weightField);

        saveBtn = new JButton("Save");
        add(saveBtn);

        // Register the action listener for the Save button
        saveBtn.addActionListener(new SaveListener());

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Inner class that handles the Save button click.
     * Reads all fields, constructs a Profile, and shows it in a dialog.
     */
    private class SaveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String sex  = sexField.getText();
            int age     = Integer.parseInt(ageField.getText());
            double h    = Double.parseDouble(heightField.getText());
            double w    = Double.parseDouble(weightField.getText());

            Profile p   = new Profile(name, sex, age, h, w, "metric");
            JOptionPane.showMessageDialog(
                ProfileUI.this,
                p.toString(),
                "Profile Saved",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * Launches the Profile UI.
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProfileUI ui = new ProfileUI();
            ui.setLocationRelativeTo(null);
            ui.setVisible(true);
        });
    }
}