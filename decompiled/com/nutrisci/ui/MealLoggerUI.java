/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import com.nutrisci.dao.MealDAO;
import com.nutrisci.dao.MySQLMealDAO;
import com.nutrisci.dao.MySQLNutritionDAO;
import com.nutrisci.model.Meal;
import com.nutrisci.service.AnalysisModule;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MealLoggerUI
extends JFrame {
    private final JTextField tfProfileId = new JTextField();
    private final JComboBox<String> cbMealType = new JComboBox<String>(new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});
    private final JTextField tfLoggedAt = new JTextField();
    private final JTextArea taIngredients = new JTextArea(5, 20);
    private final JButton btnSave = new JButton("Save");
    private final JButton btnClear = new JButton("Clear");
    private final MealDAO dao;
    private final AnalysisModule analysis;

    public MealLoggerUI(MealDAO dao) {
        this.dao = dao;
        this.analysis = new AnalysisModule(new MySQLNutritionDAO());
        this.initComponents();
    }

    private void initComponents() {
        this.setTitle("Meal Logger");
        this.setDefaultCloseOperation(3);
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = 2;
        gbc.anchor = 17;
        int y = 0;
        gbc.gridx = 0;
        gbc.gridy = y++;
        this.add((Component)new JLabel("Profile ID:"), gbc);
        gbc.gridx = 1;
        this.add((Component)this.tfProfileId, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        this.add((Component)new JLabel("Meal Type:"), gbc);
        gbc.gridx = 1;
        this.add(this.cbMealType, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        this.add((Component)new JLabel("Logged At (yyyy-MM-dd HH:mm):"), gbc);
        gbc.gridx = 1;
        this.tfLoggedAt.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        this.add((Component)this.tfLoggedAt, gbc);
        gbc.gridx = 0;
        gbc.gridy = y++;
        gbc.anchor = 18;
        this.add((Component)new JLabel("<html>Ingredients:<br/>(one per line as name:grams)</html>"), gbc);
        gbc.gridx = 1;
        this.add((Component)new JScrollPane(this.taIngredients), gbc);
        JPanel btnPanel = new JPanel(new FlowLayout(0, 8, 0));
        btnPanel.add(this.btnSave);
        btnPanel.add(this.btnClear);
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.anchor = 10;
        this.add((Component)btnPanel, gbc);
        this.btnSave.addActionListener(new SaveListener());
        this.btnClear.addActionListener(e -> this.clearFields());
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void clearFields() {
        this.tfProfileId.setText("");
        this.cbMealType.setSelectedIndex(0);
        this.tfLoggedAt.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        this.taIngredients.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MySQLMealDAO dao = new MySQLMealDAO();
            new MealLoggerUI(dao);
        });
    }

    private class SaveListener
    implements ActionListener {
        private SaveListener() {
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            try {
                String[] lines;
                Meal m = new Meal();
                m.setProfileId(Integer.parseInt(MealLoggerUI.this.tfProfileId.getText().trim()));
                m.setType((String)MealLoggerUI.this.cbMealType.getSelectedItem());
                LocalDateTime dt = LocalDateTime.parse(MealLoggerUI.this.tfLoggedAt.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                m.setLoggedAt(dt);
                HashMap<String, Double> ingredients = new HashMap<String, Double>();
                String[] stringArray = lines = MealLoggerUI.this.taIngredients.getText().split("\\r?\\n");
                int n = lines.length;
                int n2 = 0;
                while (n2 < n) {
                    String line = stringArray[n2];
                    if (!line.isBlank()) {
                        String[] parts = line.split(":");
                        if (parts.length != 2) {
                            throw new IllegalArgumentException("Invalid ingredient format: " + line);
                        }
                        String name = parts[0].trim();
                        double qty = Double.parseDouble(parts[1].trim());
                        ingredients.put(name, qty);
                    }
                    ++n2;
                }
                m.setIngredients(ingredients);
                MealLoggerUI.this.dao.insert(m);
                double totalCals = MealLoggerUI.this.analysis.computeTotalCalories(List.of(m));
                JOptionPane.showMessageDialog(MealLoggerUI.this, String.format("Saved meal with ID: %d%nTotal calories: %.2f kcal", m.getId(), totalCals), "Success", 1);
                MealLoggerUI.this.clearFields();
            }
            catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(MealLoggerUI.this, "Please enter valid numbers for Profile ID and ingredient grams.", "Input Error", 0);
            }
            catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(MealLoggerUI.this, iae.getMessage(), "Format Error", 0);
            }
            catch (SQLException sqle) {
                JOptionPane.showMessageDialog(MealLoggerUI.this, "Database error:\n" + sqle.getMessage(), "DB Error", 0);
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(MealLoggerUI.this, "Unexpected error:\n" + ex.getMessage(), "Error", 0);
            }
        }
    }
}
