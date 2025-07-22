package com.nutrisci.ui;

import com.nutrisci.dao.DAOFactory;
import com.nutrisci.dao.MealDAO;
import com.nutrisci.dao.NutritionDAOImpl; 
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
import java.util.Map;

public class MealLoggerUI extends JPanel {
    private int profileId = 1; 

    private final JTextField tfProfileId   = new JTextField();
    private final JComboBox<String> cbMealType = new JComboBox<>(
        new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});
    private final JTextField tfLoggedAt     = new JTextField();
    private final JTextArea taIngredients   = new JTextArea(5, 20);
    private final JButton btnSave           = new JButton("Save");
    private final JButton btnClear          = new JButton("Clear");

    private final MealDAO dao;
    private final AnalysisModule analysis;

    public MealLoggerUI() {
        this.dao = DAOFactory.getMealDAO(); 
        this.analysis = new AnalysisModule(new NutritionDAOImpl());
        initializeUI();
    }

    public MealLoggerUI(MealDAO dao) {
        this.dao = dao;
        this.analysis = new AnalysisModule(new NutritionDAOImpl());
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("UC2: Meal Logger"));
        
        JLabel titleLabel = new JLabel("Meal Logging Interface", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Profile ID display
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Profile ID:"), gbc);
        gbc.gridx = 1;
        JLabel profileLabel = new JLabel(String.valueOf(profileId));
        controlPanel.add(profileLabel, gbc);
        
        // Meal name input
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("Meal Name:"), gbc);
        gbc.gridx = 1;
        JTextField mealNameField = new JTextField(20);
        controlPanel.add(mealNameField, gbc);
        
        // Food items input
        gbc.gridx = 0; gbc.gridy = 2;
        controlPanel.add(new JLabel("Food Items:"), gbc);
        gbc.gridx = 1;
        JTextArea foodItemsArea = new JTextArea(5, 20);
        foodItemsArea.setBorder(BorderFactory.createLoweredBevelBorder());
        controlPanel.add(new JScrollPane(foodItemsArea), gbc);
        
        // Log button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton logButton = new JButton("Log Meal");
        logButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Meal logged successfully!\n" +
                "Profile: " + profileId + "\n" +
                "Meal: " + mealNameField.getText(), 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        controlPanel.add(logButton, gbc);
        
        add(controlPanel, BorderLayout.CENTER);
        
        // Status area
        JTextArea statusArea = new JTextArea(3, 50);
        statusArea.setEditable(false);
        statusArea.setText("Ready to log meals for Profile " + profileId);
        statusArea.setBackground(getBackground());
        add(statusArea, BorderLayout.SOUTH);
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

                // compute total calories manually using working NutritionDAOImpl
                double totalCals = 0.0;
                NutritionDAOImpl nutritionDAO = new NutritionDAOImpl();
                
                System.out.println("=== Computing calories ===");
                for (Map.Entry<String, Double> entry : m.getIngredients().entrySet()) {
                    String foodName = entry.getKey();
                    double grams = entry.getValue();
                    
                    try {
                        double caloriesPerGram = nutritionDAO.getCaloriesPerGram(foodName);
                        double ingredientCalories = caloriesPerGram * grams;
                        totalCals += ingredientCalories;
                        
                        System.out.println(foodName + ": " + caloriesPerGram + " cal/g × " + grams + "g = " + ingredientCalories + " calories");
                    } catch (Exception e) {
                        System.out.println("Error getting calories for '" + foodName + "': " + e.getMessage());
                    }
                }
                
                System.out.println("Total calories: " + totalCals);

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

    public void setProfile(int profileId) {
        this.profileId = profileId;
        Component[] components = getComponents();
        updateProfileInComponents(this, profileId);
    }

    private void updateProfileInComponents(Container container, int newProfileId) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getText().matches("\\d+")) {
                    label.setText(String.valueOf(newProfileId));
                }
            } else if (comp instanceof Container) {
                updateProfileInComponents((Container) comp, newProfileId);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MealDAO dao = new MySQLMealDAO();
            new MealLoggerUI(dao);
        });
    }
}
