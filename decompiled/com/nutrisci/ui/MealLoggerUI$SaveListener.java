/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import com.nutrisci.model.Meal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;

private class MealLoggerUI.SaveListener
implements ActionListener {
    private MealLoggerUI.SaveListener() {
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
