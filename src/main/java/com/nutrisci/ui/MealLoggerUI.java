package com.nutrisci.ui;

import com.nutrisci.dao.DAOFactory;
import com.nutrisci.dao.MealDAO;
import com.nutrisci.dao.NutritionDAO;
import com.nutrisci.dao.NutritionDAOImpl; 
import com.nutrisci.model.Meal;

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
    private int profileId;

    private JTextField tfProfileId   = new JTextField();
    private JComboBox<String> cbMealType = new JComboBox<>(
        new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});
    private JTextField tfLoggedAt     = new JTextField();
    private JTextArea taIngredients   = new JTextArea(5, 20);
    private JButton btnSave           = new JButton("Save");
    private JButton btnClear          = new JButton("Clear");

    private MealDAO dao;

    public MealLoggerUI() {
        dao = DAOFactory.getMealDAO(); 
        initializeUI();
    }

    public MealLoggerUI(MealDAO dao) {
        this.dao = dao;
        initializeUI();
    }
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Meal Logger"));
        
        JLabel titleLabel = new JLabel("Meal Logging Interface", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Profile ID (read-only display)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Profile ID:"), gbc);
        gbc.gridx = 1;
        tfProfileId.setText(profileId > 0 ? String.valueOf(profileId) : "Not Set");
        tfProfileId.setEditable(false);
        formPanel.add(tfProfileId, gbc);
        
        // Meal Type
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Meal Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbMealType, gbc);
        
        // Logged At
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Logged At (yyyy-MM-dd HH:mm):"), gbc);
        gbc.gridx = 1;
        tfLoggedAt.setText(LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        formPanel.add(tfLoggedAt, gbc);
        
        // Ingredients
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Ingredients (name:grams):"), gbc);
        gbc.gridx = 1;
        taIngredients.setBorder(BorderFactory.createLoweredBevelBorder());
        formPanel.add(new JScrollPane(taIngredients), gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        btnSave.addActionListener(new SaveListener());
        buttonPanel.add(btnSave);
        
        btnClear.addActionListener(e -> clearFields());
        buttonPanel.add(btnClear);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void clearFields() {
        tfProfileId.setText("");
        cbMealType.setSelectedIndex(0);
        tfLoggedAt.setText(LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        taIngredients.setText("");
    }

    public void setProfile(int profileId) {
        if (profileId <= 0) {
            throw new IllegalArgumentException("Profile ID must be positive");
        }
        this.profileId = profileId;
        tfProfileId.setText(String.valueOf(profileId));
        
        updateProfileInComponents(this, profileId);
    }

    private class SaveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ev) {
            if (profileId <= 0) {
                JOptionPane.showMessageDialog(MealLoggerUI.this, "No profile selected. Please set a profile first.", "Profile Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                Meal m = new Meal();
                m.setProfileId(profileId);
                m.setType((String) cbMealType.getSelectedItem());
                m.setLoggedAt(LocalDateTime.parse(tfLoggedAt.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

                Map<String, Double> ingredients = new HashMap<>();
                String[] lines = taIngredients.getText().split("\\r?\\n");
                for (String line : lines) {
                    if (line.isBlank()) continue;
                    String[] parts = line.split(":");
                    if (parts.length != 2) throw new IllegalArgumentException("Invalid ingredient format: " + line);
                    ingredients.put(parts[0].trim(), Double.parseDouble(parts[1].trim()));
                }
                m.setIngredients(ingredients);

                dao.insert(m);
                double totalCals = 0.0;
                NutritionDAO nutritionDAO = new NutritionDAOImpl();
                StringBuilder calorieDetails = new StringBuilder();
                
                for (Map.Entry<String, Double> entry : m.getIngredients().entrySet()) {
                    String foodName = entry.getKey();
                    double grams = entry.getValue();
                    
                    try {
                        double caloriesPer100g = nutritionDAO.getNutrientInfo(foodName).getCaloriesPerGram() * 100;
                        double ingredientCalories = (caloriesPer100g / 100.0) * grams;
                        totalCals += ingredientCalories;
                        calorieDetails.append(String.format("- %s (%.0fg): %.2f kcal\n", foodName, grams, ingredientCalories));
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(
                            MealLoggerUI.this,
                            "Could not calculate calories.\nNutritional data not found for: '" + foodName + "'\n\nPlease check the spelling or add the food to the nutrition database.",
                            "Nutrient Not Found",
                            JOptionPane.ERROR_MESSAGE
                        );
                        return; // Stop processing
                    }
                }

                JOptionPane.showMessageDialog(
                    MealLoggerUI.this,
                    String.format(
                        "Saved meal with ID: %d\n\nCalorie Breakdown:\n%s\nTotal Calories: %.2f kcal",
                        m.getId(), calorieDetails.toString(), totalCals
                    ),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                clearFields();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MealLoggerUI.this, "Error saving meal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
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
            try {
                // Create the UI
                MealDAO dao = DAOFactory.getMealDAO();
                MealLoggerUI mealLoggerUI = new MealLoggerUI(dao);
                
                // Create and configure the frame
                JFrame frame = new JFrame("Meal Logger UI Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(mealLoggerUI);
                frame.setSize(600, 500);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
                System.out.println("✓ MealLoggerUI launched successfully!");
                
            } catch (Exception e) {
                System.err.println("❌ Failed to launch MealLoggerUI: " + e.getMessage());
                e.printStackTrace();
                
                // Show error dialog
                JOptionPane.showMessageDialog(null, 
                    "Failed to launch MealLoggerUI:\n" + e.getMessage(), 
                    "Launch Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
