package main.java.com.nutrisci.ui;


import main.java.com.nutrisci.model.Profile;
import main.java.com.nutrisci.dao.NutritionDAO;
import main.java.com.nutrisci.dao.InMemoryNutritionDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProfileUI extends JFrame {
    private JTextField nameField, sexField, ageField, heightField, weightField;
    private JButton saveBtn;

    public ProfileUI() {
        super("Create Profile");
        setLayout(new GridLayout(6,2));
        add(new JLabel("Name:")); nameField = new JTextField(); add(nameField);
        add(new JLabel("Sex:")); sexField = new JTextField(); add(sexField);
        add(new JLabel("Age:")); ageField = new JTextField(); add(ageField);
        add(new JLabel("Height (cm):")); heightField = new JTextField(); add(heightField);
        add(new JLabel("Weight (kg):")); weightField = new JTextField(); add(weightField);
        saveBtn = new JButton("Save"); add(saveBtn);
        saveBtn.addActionListener(new SaveListener());
        pack(); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private class SaveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String sex  = sexField.getText();
            int age     = Integer.parseInt(ageField.getText());
            double h    = Double.parseDouble(heightField.getText());
            double w    = Double.parseDouble(weightField.getText());
            Profile p   = new Profile(name, sex, age, h, w, "metric");
            JOptionPane.showMessageDialog(ProfileUI.this, p.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProfileUI().setVisible(true));
    }
}
