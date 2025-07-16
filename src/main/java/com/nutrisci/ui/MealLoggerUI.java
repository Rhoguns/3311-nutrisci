package com.nutrisci.ui;

import com.nutrisci.dao.MealDAO;
import com.nutrisci.dao.MySQLMealDAO;
import com.nutrisci.dao.MySQLNutritionDAO;
import com.nutrisci.model.Meal;
import com.nutrisci.service.AnalysisModule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealLoggerUI extends JFrame {
    private final JTextField tfProfileId   = new JTextField();
    private final JComboBox<String> cbMealType = new JComboBox<>(
        new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});
    private final JTextField tfLoggedAt     = new JTextField();
    private final JTextArea taIngredients   = new JTextArea(5, 20);
    private final JButton btnSave           = new JButton("Save");
    private final JButton btnClear          = new JButton("Clear");

    private final MealDAO dao;
    private final AnalysisModule analysis;

    public MealLoggerUI(MealDAO dao) {
        this.dao = dao;
        // Use the real CNF-backed nutrition DAO for calorie calculations
        this.analysis = new AnalysisModule(new MySQLNutritionDAO());
        initComponents();
    }

    private void initComponents() {
        setTitle("Meal Logger");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;

        // Profile ID
        gbc.gridx = 0; gbc.gridy = y;
        add(new JLabel("Profile ID:"), gbc);
        gbc.gridx = 1;
        add(tfProfileId, gbc);
        y++;

        // Label and combo box for selecting the meal type.
        // Meal Type
        gbc.gridx = 0; gbc.gridy = y;
        add(new JLabel("Meal Type:"), gbc);
        gbc.gridx = 1;
        add(cbMealType, gbc);
        y++;

        // Logged At
        gbc.gridx = 0; gbc.gridy = y;
        add(new JLabel("Logged At (yyyy-MM-dd HH:mm):"), gbc);
        gbc.gridx = 1;
        tfLoggedAt.setText(LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        add(tfLoggedAt, gbc);
        y++;

        // Ingredients
        gbc.gridx = 0; gbc.gridy = y;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("<html>Ingredients:<br/>(one per line as name:grams)</html>"), gbc);
        gbc.gridx = 1;
        add(new JScrollPane(taIngredients), gbc);
        y++;

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.add(btnSave);
        btnPanel.add(btnClear);
        gbc.gridx = 0; gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.anchor    = GridBagConstraints.CENTER;
        add(btnPanel, gbc);

        // Listeners
        btnSave.addActionListener(new SaveListener());
        btnClear.addActionListener(e -> clearFields());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void clearFields() {
        tfProfileId.setText("");
        cbMealType.setSelectedIndex(0);
        tfLoggedAt.setText(LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        taIngredients.setText("");
    }

    private class SaveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ev) {
            try {
                Meal m = new Meal();
                m.setProfileId(Integer.parseInt(tfProfileId.getText().trim()));
                m.setType((String) cbMealType.getSelectedItem());
                LocalDateTime dt = LocalDateTime.parse(
                    tfLoggedAt.getText().trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                );
                m.setLoggedAt(dt);

                // parse ingredients (one per line: name:grams)
                Map<String, Double> ingredients = new HashMap<>();
                String[] lines = taIngredients.getText().split("\\r?\\n");
                for (String line : lines) {
                    if (line.isBlank()) continue;
                    String[] parts = line.split(":");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Invalid ingredient format: " + line);
                    }
                    String name = parts[0].trim();
                    double qty = Double.parseDouble(parts[1].trim());
                    ingredients.put(name, qty);
                }
                m.setIngredients(ingredients);

                // save to DB
                dao.insert(m);

                // compute total calories using real CNF data
                double totalCals = analysis.computeTotalCalories(List.of(m));

                JOptionPane.showMessageDialog(
                    MealLoggerUI.this,
                    String.format(
                        "Saved meal with ID: %d%nTotal calories: %.2f kcal",
                        m.getId(), totalCals
                    ),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                clearFields();

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(
                    MealLoggerUI.this,
                    "Please enter valid numbers for Profile ID and ingredient grams.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(
                    MealLoggerUI.this,
                    iae.getMessage(),
                    "Format Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(
                    MealLoggerUI.this,
                    "Database error:\n" + sqle.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    MealLoggerUI.this,
                    "Unexpected error:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MealDAO dao = new MySQLMealDAO();  // JDBC-backed DAO
            new MealLoggerUI(dao);
        });
    }
}
