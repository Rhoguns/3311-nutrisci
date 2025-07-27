package com.nutrisci.ui;

import com.nutrisci.dao.DAOFactory;
import com.nutrisci.dao.MealDAO;
import com.nutrisci.dao.NutritionDAO;
import com.nutrisci.info.NutrientInfo;
import com.nutrisci.model.Meal;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class CompareMealsPanel
extends JPanel {
    private JComboBox<Meal> meal1ComboBox;
    private JComboBox<Meal> meal2ComboBox;
    private JLabel statusLabel; // Status label for user feedback
    private JTextArea resultsArea; // Results area for comparison output
    private JScrollPane scrollPane; // Scroll pane for results area

    private MealDAO mealDao;
    private NutritionDAO nutritionDao;
    private int profileId;

    public CompareMealsPanel() {
        try {
            mealDao = DAOFactory.getMealDAO();
            nutritionDao = DAOFactory.getNutritionDAO();
            initComponents();
        } catch (Exception err) {
            setLayout(new BorderLayout());
            add(new JLabel("Error loading panel"), BorderLayout.CENTER);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Compare Meals", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Selection panel
        JPanel selectionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Meal 1 selection
        gbc.gridx = 0; gbc.gridy = 0;
        selectionPanel.add(new JLabel("Meal 1:"), gbc);
        
        meal1ComboBox = new JComboBox<>();
        meal1ComboBox.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1; gbc.gridy = 0;
        selectionPanel.add(meal1ComboBox, gbc);
        
        // Meal 2 selection
        gbc.gridx = 0; gbc.gridy = 1;
        selectionPanel.add(new JLabel("Meal 2:"), gbc);
        
        meal2ComboBox = new JComboBox<>();
        meal2ComboBox.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1; gbc.gridy = 1;
        selectionPanel.add(meal2ComboBox, gbc);
        
        // Compare button
        JButton compareButton = new JButton("Compare Meals");
        compareButton.addActionListener(e -> compareMeals());
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        selectionPanel.add(compareButton, gbc);
        
        mainPanel.add(selectionPanel, BorderLayout.CENTER);
        
        // Status label at bottom
        statusLabel = new JLabel("Select a profile to load meals", JLabel.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // Results area (initially hidden)
        resultsArea = new JTextArea(15, 50);
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Comparison Results"));
        scrollPane.setVisible(false);
        
        add(mainPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setProfile(int profileId) {
        this.profileId = profileId;
        statusLabel.setText("Loading meals for profile " + profileId + "...");
        SwingUtilities.invokeLater(() -> loadMealsForProfile());
    }

    private void loadMealsForProfile() {
        if (profileId <= 0) {
            statusLabel.setText("❌ No profile selected");
            return;
        }
        
        try {
            // Clear existing meals first
            meal1ComboBox.removeAllItems();
            meal2ComboBox.removeAllItems();
            
            // Load meals from database
            List<Meal> meals = mealDao.findByProfileId(profileId);
            
            if (meals.isEmpty()) {
                statusLabel.setText("❌ No meals found for this profile");
                return;
            }
            
            // Add meals to dropdowns
            for (Meal meal : meals) {
                meal1ComboBox.addItem(meal);
                meal2ComboBox.addItem(meal);
            }
            
            statusLabel.setText("✅ Loaded " + meals.size() + " meals");
            
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("❌ Error loading meals: " + e.getMessage());
        }
    }

    private void compareMeals() {
        Meal meal1 = (Meal) meal1ComboBox.getSelectedItem();
        Meal meal2 = (Meal) meal2ComboBox.getSelectedItem();
        
        if (meal1 == null || meal2 == null) {
            statusLabel.setText("❌ Please select both meals to compare");
            return;
        }
        
        if (meal1.getId() == meal2.getId()) {
            statusLabel.setText("❌ Please select two different meals");
            return;
        }
        
        try {
            String comparisonText = generateComparisonText(meal1, meal2);
            resultsArea.setText(comparisonText); 

            scrollPane.setVisible(true);
            
            statusLabel.setText("✅ Comparison completed");
            revalidate();
            repaint();
            
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("❌ Error comparing meals: " + e.getMessage());
        }
    }

    private String generateComparisonText(Meal meal1, Meal meal2) throws SQLException {
        StringBuilder sb = new StringBuilder();
        
        sb.append("MEAL COMPARISON\n");
        sb.append("=".repeat(60)).append("\n\n");
        

        sb.append(String.format("MEAL 1: %s (%s)\n", meal1.getType(), meal1.getLoggedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        sb.append(String.format("MEAL 2: %s (%s)\n\n", meal2.getType(), meal2.getLoggedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        
        sb.append("INGREDIENTS COMPARISON\n");
        sb.append("-".repeat(55)).append("\n");
        
        java.util.Set<String> allIngredients = new java.util.TreeSet<>();
        allIngredients.addAll(meal1.getIngredients().keySet());
        allIngredients.addAll(meal2.getIngredients().keySet());
        
        sb.append(String.format("%-20s %10s %10s %10s\n", "INGREDIENT", "MEAL 1", "MEAL 2", "DIFF"));
        sb.append("-".repeat(55)).append("\n");
        
        for (String ingredient : allIngredients) {
            double qty1 = 0.0;
            if (meal1.getIngredients().containsKey(ingredient)) {
                qty1 = meal1.getIngredients().get(ingredient);
            }
            
            double qty2 = 0.0;
            if (meal2.getIngredients().containsKey(ingredient)) {
                qty2 = meal2.getIngredients().get(ingredient);
            }
            sb.append(String.format("%-20s %8.1fg %8.1fg %+9.1fg\n", 
                ingredient.substring(0, Math.min(19, ingredient.length())), qty1, qty2, qty1 - qty2));
        }
        

        sb.append("\nNUTRITIONAL COMPARISON\n");
        sb.append("-".repeat(50)).append("\n");
        
        NutritionSummary nutrition1 = calculateMealNutrition(meal1);
        NutritionSummary nutrition2 = calculateMealNutrition(meal2);
        
        sb.append(String.format("%-15s %10s %10s %10s\n", "NUTRIENT", "MEAL 1", "MEAL 2", "DIFF"));
        sb.append("-".repeat(50)).append("\n");
        
        sb.append(String.format("%-15s %9.1f %9.1f %+9.1f\n", "Calories", nutrition1.calories, nutrition2.calories, nutrition1.calories - nutrition2.calories));
        sb.append(String.format("%-15s %8.1fg %8.1fg %+8.1fg\n", "Protein", nutrition1.protein, nutrition2.protein, nutrition1.protein - nutrition2.protein));
        sb.append(String.format("%-15s %8.1fg %8.1fg %+8.1fg\n", "Carbs", nutrition1.carbs, nutrition2.carbs, nutrition1.carbs - nutrition2.carbs));
        sb.append(String.format("%-15s %8.1fg %8.1fg %+8.1fg\n", "Fat", nutrition1.fat, nutrition2.fat, nutrition1.fat - nutrition2.fat));
        sb.append(String.format("%-15s %8.1fg %8.1fg %+8.1fg\n", "Fiber", nutrition1.fiber, nutrition2.fiber, nutrition1.fiber - nutrition2.fiber));
        
        return sb.toString();
    }

    private NutritionSummary calculateMealNutrition(Meal meal) throws SQLException {
        NutritionSummary summary = new NutritionSummary();
        for (Map.Entry<String, Double> ingredient : meal.getIngredients().entrySet()) {
            NutrientInfo nutritionInfo = nutritionDao.getNutrientInfo(ingredient.getKey());
            if (nutritionInfo != null) {
                summary.calories += nutritionInfo.getCaloriesPerGram() * ingredient.getValue(); // Calories are per gram
                summary.protein += nutritionInfo.getProteinPerGram() * ingredient.getValue();
                summary.carbs += nutritionInfo.getCarbsPerGram() * ingredient.getValue();
                summary.fat += nutritionInfo.getFatPerGram() * ingredient.getValue();
             }
        }
        return summary;
    }

    private static class NutritionSummary {
        double calories = 0, protein = 0, carbs = 0, fat = 0, fiber = 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            JFrame frame = new JFrame("NutriSci - Compare Meals");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            CompareMealsPanel panel = new CompareMealsPanel();
            
            // Pre-load profile 1 for demonstration
            panel.setProfile(1);
            
            frame.add(panel);
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
