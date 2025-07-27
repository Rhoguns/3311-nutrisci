package com.nutrisci.ui;

import com.nutrisci.dao.DAOFactory;
import com.nutrisci.dao.MealDAO;
import com.nutrisci.dao.NutritionDAO;
import com.nutrisci.dao.SwapRuleDAO;
import com.nutrisci.logic.SwapEngine;
import com.nutrisci.model.Meal;
import com.nutrisci.info.NutrientInfo;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class MealJournalUI extends JPanel {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // UI Components
    private JLabel statusLabel;
    private JComboBox<Meal> cbMeals;
    private JComboBox<String> cbGoal;
    private JComboBox<String> cbIngredients;
    private JTextField tfProfileId;
    private JButton btnLoadMeals;
    private JButton btnGetSuggestions;
    private JButton btnApplySwap;
    private JPanel taIngredients;
    
    // Business Logic
    private MealDAO mealDao;
    private SwapRuleDAO swapRuleDao;
    private NutritionDAO nutritionDao;
    private SwapEngine swapEngine;

    public MealJournalUI() {
        this.mealDao = DAOFactory.getMealDAO();
        this.swapRuleDao = DAOFactory.getSwapRuleDAO();
        this.nutritionDao = DAOFactory.getNutritionDAO();
        
        this.swapEngine = new SwapEngine(swapRuleDao, mealDao);
        initializeComponents();
        setupLayout();
        setupMealRenderer();
        setupEventHandlers();
    }

    private void initializeComponents() {
        statusLabel = new JLabel("Ready - Enter a Profile ID and click 'Load Meals'");
        cbMeals = new JComboBox<>();
        cbGoal = new JComboBox<>(new String[]{"reduce calories", "increase protein", "reduce fat", "increase fiber"});
        cbIngredients = new JComboBox<>();
        tfProfileId = new JTextField("1", 10);
        
        btnLoadMeals = new JButton("Load Meals");
        btnGetSuggestions = new JButton("Get Swap Suggestions");
        btnApplySwap = new JButton("Apply Selected Swap");
        
        taIngredients = new JPanel();
        taIngredients.setBorder(BorderFactory.createTitledBorder("Current Meal Ingredients"));
        taIngredients.setLayout(new BorderLayout());
        
        btnGetSuggestions.setEnabled(false);
        btnApplySwap.setEnabled(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top panel - Profile selection and meal loading
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Profile & Meal Selection"));
        topPanel.add(new JLabel("Profile ID:"));
        topPanel.add(tfProfileId);
        topPanel.add(btnLoadMeals);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Select Meal:"));
        topPanel.add(cbMeals);

        // Center panel - Just ingredients with swap functionality
        add(topPanel, BorderLayout.NORTH);
        add(taIngredients, BorderLayout.CENTER);

        // Bottom panel - Status
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(new JLabel("Status: "));
        bottomPanel.add(statusLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        btnLoadMeals.addActionListener(this::onLoadMeals);
        cbMeals.addActionListener(e -> onMealSelected());
        btnGetSuggestions.addActionListener(this::onGetSuggestions);
        btnApplySwap.addActionListener(this::onApplySwap);
    }

    private void onMealSelected() {
        Meal selectedMeal = (Meal) cbMeals.getSelectedItem();
        if (selectedMeal == null) {
            cbIngredients.removeAllItems();
            taIngredients.removeAll();
            btnGetSuggestions.setEnabled(false);
            btnApplySwap.setEnabled(false);
            return;
        }

        JPanel ingredientsPanel = createInteractiveIngredientsPanel(selectedMeal);
        
        taIngredients.removeAll();
        taIngredients.setLayout(new BorderLayout());
        taIngredients.add(new JScrollPane(ingredientsPanel), BorderLayout.CENTER);
        taIngredients.revalidate();
        taIngredients.repaint();
        
        btnGetSuggestions.setEnabled(true);
        btnApplySwap.setEnabled(false);
        
        statusLabel.setText("Meal selected: " + selectedMeal.getType() + " - " + selectedMeal.getIngredients().size() + " ingredients");
    }

    private JPanel createInteractiveIngredientsPanel(Meal meal) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Current Meal Ingredients"));
        
        // Add goal selection at the top
        JPanel goalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        goalPanel.add(new JLabel("Nutritional Goal:"));
        goalPanel.add(cbGoal);
        panel.add(goalPanel);
        
        panel.add(Box.createVerticalStrut(10));
        
        // Add each ingredient with its own swap button
        for (Map.Entry<String, Double> entry : meal.getIngredients().entrySet()) {
            String ingredient = entry.getKey();
            Double quantity = entry.getValue();
            
            JPanel ingredientRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            ingredientRow.setBorder(BorderFactory.createEtchedBorder());
            
            // Ingredient label
            JLabel ingredientLabel = new JLabel(String.format("%s (%.0fg)", ingredient, quantity));
            ingredientLabel.setPreferredSize(new Dimension(200, 25));
            ingredientRow.add(ingredientLabel);
            
            // Suggest & Apply Swap button for this specific ingredient
            JButton swapButton = new JButton("Suggest & Apply Swap");
            swapButton.addActionListener(e -> suggestAndApplySwapForIngredient(ingredient, quantity, meal));
            ingredientRow.add(swapButton);
            
            panel.add(ingredientRow);
        }
        
        return panel;
    }

    private void suggestAndApplySwapForIngredient(String ingredient, double quantity, Meal originalMeal) {
        String selectedGoal = (String) cbGoal.getSelectedItem();
        statusLabel.setText("Getting suggestions for " + ingredient + "...");

        try {
            // Test the swap engine
            String suggestion = swapEngine.suggestSwap(ingredient, selectedGoal);

            if (suggestion == null || suggestion.equals("No suggestion available")) {
                statusLabel.setText("No swaps available for " + ingredient);
                
                String debugMessage = "No suitable swaps found for " + ingredient + 
                        "\n\nGoal: " + selectedGoal +
                        "\n\nCheck console for debug details." +
                        "\n\nPossible issues:" +
                        "\n• Food name case mismatch (try 'bread' vs 'Bread')" +
                        "\n• No swap rules in database" +
                        "\n• SwapEngine using hardcoded data only";
                
                JOptionPane.showMessageDialog(this, debugMessage,
                        "No Suggestions Available", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Convert single suggestion to list for compatibility
            List<String> suggestions = List.of(suggestion);
            
            // Show nutritional analysis dialog
            showNutritionalSwapAnalysis(ingredient, quantity, suggestions, selectedGoal, originalMeal);

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error during swap operation");
            JOptionPane.showMessageDialog(this,
                    "Error getting swap suggestions:\n" + e.getMessage(),
                    "Swap Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showNutritionalSwapAnalysis(String originalFood, double quantity, 
                                           List<String> suggestions, String goal, Meal originalMeal) {
        try {
            JDialog analysisDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                    "Nutritional Swap Analysis", true);
            analysisDialog.setLayout(new BorderLayout());
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            
            // Title panel
            JPanel titlePanel = new JPanel(new FlowLayout());
            titlePanel.add(new JLabel(String.format("Analyzing swaps for %s (%.0fg) - Goal: %s", 
                    originalFood, quantity, goal)));
            mainPanel.add(titlePanel, BorderLayout.NORTH);
            
            // Nutrition comparison panel
            JPanel comparisonPanel = createNutritionComparisonPanel(originalFood, quantity, suggestions);
            mainPanel.add(new JScrollPane(comparisonPanel), BorderLayout.CENTER);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton btnClose = new JButton("Close");
            JButton btnApplyBest = new JButton("Apply Best Suggestion");
            JButton btnViewCharts = new JButton("View Charts");
            
            btnClose.addActionListener(e -> analysisDialog.dispose());
            btnApplyBest.addActionListener(e -> {
                if (!suggestions.isEmpty()) {
                    String bestSuggestion = suggestions.get(0);
                    analysisDialog.dispose();
                    applySingleSwap(originalFood, quantity, goal, originalMeal, bestSuggestion);
                }
            });
            btnViewCharts.addActionListener(e -> showNutritionalCharts(originalFood, quantity, suggestions));
            
            buttonPanel.add(btnViewCharts);
            buttonPanel.add(btnApplyBest);
            buttonPanel.add(btnClose);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            analysisDialog.add(mainPanel);
            analysisDialog.setSize(800, 600);
            analysisDialog.setLocationRelativeTo(this);
            analysisDialog.setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating analysis: " + e.getMessage());
        }
    }

    private JPanel createNutritionComparisonPanel(String originalFood, double quantity, List<String> suggestions) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        try {
            // Get original food nutrition
            double origCalories = nutritionDao.getNutrientInfo(originalFood).getCaloriesPerGram() * quantity;
            NutrientInfo origNutrients = nutritionDao.getNutrientInfo(originalFood);
            double origProtein = origNutrients.getProteinPerGram() * quantity;
            double origCarbs = origNutrients.getCarbsPerGram() * quantity;
            double origFat = origNutrients.getFatPerGram() * quantity;
            double origFiber = 0.0; // Fiber data not available in NutrientInfo
            
            // Original food panel
            JPanel originalPanel = createFoodNutritionPanel(
                String.format("ORIGINAL: %s (%.0fg)", originalFood, quantity),
                origCalories, origProtein, origCarbs, origFat, origFiber,
                Color.LIGHT_GRAY
            );
            panel.add(originalPanel);
            panel.add(Box.createVerticalStrut(10));
            
            // Suggestion panels
            for (int i = 0; i < Math.min(suggestions.size(), 5); i++) {
                String suggestion = suggestions.get(i);
                String suggestedFood = suggestion.split(" \\(")[0].trim();
                
                try {
                    double sugCalories = nutritionDao.getNutrientInfo(suggestedFood).getCaloriesPerGram() * quantity;
                    NutrientInfo sugNutrients = nutritionDao.getNutrientInfo(suggestedFood);
                    double sugProtein = sugNutrients.getProteinPerGram() * quantity;
                    double sugCarbs = sugNutrients.getCarbsPerGram() * quantity;
                    double sugFat = sugNutrients.getFatPerGram() * quantity;
                    double sugFiber = 0.0; // Fiber data not available in NutrientInfo
                    
                    // Calculate deltas
                    double calDelta = sugCalories - origCalories;
                    double proteinDelta = sugProtein - origProtein;
                    double carbsDelta = sugCarbs - origCarbs;
                    double fatDelta = sugFat - origFat;
                    double fiberDelta = sugFiber - origFiber;
                    
                    // Choose color based on improvement
                    Color panelColor = calDelta < 0 ? new Color(200, 255, 200) : new Color(255, 220, 220);
                    
                    JPanel suggestionPanel = createFoodNutritionPanelWithDeltas(
                        String.format("OPTION %d: %s (%.0fg)", i + 1, suggestedFood, quantity),
                        sugCalories, sugProtein, sugCarbs, sugFat, sugFiber,
                        calDelta, proteinDelta, carbsDelta, fatDelta, fiberDelta,
                        panelColor
                    );
                    panel.add(suggestionPanel);
                    panel.add(Box.createVerticalStrut(5));
                    
                } catch (Exception e) {
                    JPanel errorPanel = new JPanel();
                    errorPanel.add(new JLabel("Error getting nutrition for: " + suggestedFood));
                    panel.add(errorPanel);
                }
            }
            
        } catch (Exception e) {
            JPanel errorPanel = new JPanel();
            errorPanel.add(new JLabel("Error creating nutrition comparison: " + e.getMessage()));
            panel.add(errorPanel);
        }
        
        return panel;
    }

    private JPanel createFoodNutritionPanel(String title, double calories, double protein, 
                                          double carbs, double fat, double fiber, Color bgColor) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(bgColor);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel(String.format("Calories: %.1f kcal", calories)), gbc);
        
        gbc.gridx = 1;
        panel.add(new JLabel(String.format("Protein: %.1fg", protein)), gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel(String.format("Carbs: %.1fg", carbs)), gbc);
        
        gbc.gridx = 3;
        panel.add(new JLabel(String.format("Fat: %.1fg", fat)), gbc);
        
        gbc.gridx = 4;
        panel.add(new JLabel(String.format("Fiber: %.1fg", fiber)), gbc);
        
        return panel;
    }

    private JPanel createFoodNutritionPanelWithDeltas(String title, double calories, double protein, 
                                                    double carbs, double fat, double fiber,
                                                    double calDelta, double proteinDelta, double carbsDelta, 
                                                    double fatDelta, double fiberDelta, Color bgColor) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(bgColor);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 8, 3, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // First row - absolute values
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel(String.format("Calories: %.1f kcal", calories)), gbc);
        
        gbc.gridx = 1;
        panel.add(new JLabel(String.format("Protein: %.1fg", protein)), gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel(String.format("Carbs: %.1fg", carbs)), gbc);
        
        gbc.gridx = 3;
        panel.add(new JLabel(String.format("Fat: %.1fg", fat)), gbc);
        
        gbc.gridx = 4;
        panel.add(new JLabel(String.format("Fiber: %.1fg", fiber)), gbc);
        
        // Second row - deltas
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel calLabel = new JLabel(String.format("Δ: %+.1f kcal", calDelta));
        calLabel.setForeground(calDelta < 0 ? Color.GREEN.darker() : Color.RED.darker());
        panel.add(calLabel, gbc);
        
        gbc.gridx = 1;
        JLabel proteinLabel = new JLabel(String.format("Δ: %+.1fg", proteinDelta));
        proteinLabel.setForeground(proteinDelta > 0 ? Color.GREEN.darker() : Color.RED.darker());
        panel.add(proteinLabel, gbc);
        
        gbc.gridx = 2;
        JLabel carbsLabel = new JLabel(String.format("Δ: %+.1fg", carbsDelta));
        carbsLabel.setForeground(carbsDelta < 0 ? Color.GREEN.darker() : Color.RED.darker());
        panel.add(carbsLabel, gbc);
        
        gbc.gridx = 3;
        JLabel fatLabel = new JLabel(String.format("Δ: %+.1fg", fatDelta));
        fatLabel.setForeground(fatDelta < 0 ? Color.GREEN.darker() : Color.RED.darker());
        panel.add(fatLabel, gbc);
        
        gbc.gridx = 4;
        JLabel fiberLabel = new JLabel(String.format("Δ: %+.1fg", fiberDelta));
        fiberLabel.setForeground(fiberDelta > 0 ? Color.GREEN.darker() : Color.RED.darker());
        panel.add(fiberLabel, gbc);
        
        return panel;
    }

    private void showNutritionalCharts(String originalFood, double quantity, List<String> suggestions) {
        try {
            JDialog chartDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                    "Nutritional Impact Charts", true);
            chartDialog.setLayout(new BorderLayout());
            
            JTabbedPane tabbedPane = new JTabbedPane();
            
            // Calories comparison chart
            tabbedPane.addTab("Calories", createCaloriesComparisonChart(originalFood, quantity, suggestions));
            
            // Macronutrients chart
            tabbedPane.addTab("Macronutrients", createMacronutrientsChart(originalFood, quantity, suggestions));
            
            // Delta impact chart
            tabbedPane.addTab("Impact Analysis", createImpactAnalysisChart(originalFood, quantity, suggestions));
            
            chartDialog.add(tabbedPane, BorderLayout.CENTER);
            
            JButton closeBtn = new JButton("Close");
            closeBtn.addActionListener(e -> chartDialog.dispose());
            JPanel btnPanel = new JPanel();
            btnPanel.add(closeBtn);
            chartDialog.add(btnPanel, BorderLayout.SOUTH);
            
            chartDialog.setSize(900, 700);
            chartDialog.setLocationRelativeTo(this);
            chartDialog.setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating charts: " + e.getMessage());
        }
    }

    private JPanel createCaloriesComparisonChart(String originalFood, double quantity, List<String> suggestions) {
        JPanel chartPanel = new JPanel(new BorderLayout());
        
        try {
            // Create a simple bar chart representation
            JPanel barsPanel = new JPanel();
            barsPanel.setLayout(new BoxLayout(barsPanel, BoxLayout.Y_AXIS));
            
            double origCalories = nutritionDao.getNutrientInfo(originalFood).getCaloriesPerGram() * quantity;
            
            // Original food bar
            JPanel origBar = createCalorieBar("Original: " + originalFood, origCalories, origCalories, Color.GRAY);
            barsPanel.add(origBar);
            
            // Suggestion bars
            for (int i = 0; i < Math.min(suggestions.size(), 5); i++) {
                String suggestedFood = suggestions.get(i).split(" \\(")[0].trim();
                try {
                    double sugCalories = nutritionDao.getNutrientInfo(suggestedFood).getCaloriesPerGram() * quantity;
                    Color barColor = sugCalories < origCalories ? Color.GREEN : Color.RED;
                    JPanel sugBar = createCalorieBar("Option " + (i+1) + ": " + suggestedFood, 
                            sugCalories, origCalories, barColor);
                    barsPanel.add(sugBar);
                } catch (Exception e) {
                    // Skip if nutrition data not available
                }
            }
            
            chartPanel.add(new JScrollPane(barsPanel), BorderLayout.CENTER);
            
            JLabel titleLabel = new JLabel("Calorie Comparison for " + quantity + "g portions", JLabel.CENTER);
            titleLabel.setFont(titleLabel.getFont().deriveFont(16f));
            chartPanel.add(titleLabel, BorderLayout.NORTH);
            
        } catch (Exception e) {
            chartPanel.add(new JLabel("Error creating calorie chart: " + e.getMessage()));
        }
        
        return chartPanel;
    }

    private JPanel createCalorieBar(String label, double calories, double maxCalories, Color barColor) {
        JPanel barPanel = new JPanel(new BorderLayout());
        barPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel nameLabel = new JLabel(label);
        nameLabel.setPreferredSize(new Dimension(200, 25));
        barPanel.add(nameLabel, BorderLayout.WEST);
        
        // Create visual bar
        JPanel barContainer = new JPanel(new BorderLayout());
        barContainer.setPreferredSize(new Dimension(400, 25));
        barContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        int barWidth = (int) ((calories / maxCalories) * 400);
        JPanel colorBar = new JPanel();
        colorBar.setBackground(barColor);
        colorBar.setPreferredSize(new Dimension(barWidth, 23));
        barContainer.add(colorBar, BorderLayout.WEST);
        
        barPanel.add(barContainer, BorderLayout.CENTER);
        
        JLabel valueLabel = new JLabel(String.format("%.1f kcal", calories));
        valueLabel.setPreferredSize(new Dimension(80, 25));
        barPanel.add(valueLabel, BorderLayout.EAST);
        
        return barPanel;
    }

    private JPanel createMacronutrientsChart(String originalFood, double quantity, List<String> suggestions) {
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.add(new JLabel("Macronutrients comparison would go here", JLabel.CENTER));
        return chartPanel;
    }

    private JPanel createImpactAnalysisChart(String originalFood, double quantity, List<String> suggestions) {
        JPanel chartPanel = new JPanel(new BorderLayout());
        
        try {
            JTextArea analysisText = new JTextArea();
            analysisText.setEditable(false);
            analysisText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            
            StringBuilder analysis = new StringBuilder();
            analysis.append("NUTRITIONAL IMPACT ANALYSIS\n");
            analysis.append("=" .repeat(50)).append("\n\n");
            
            double origCalories = nutritionDao.getNutrientInfo(originalFood).getCaloriesPerGram() * quantity;
            NutrientInfo origNutrients = nutritionDao.getNutrientInfo(originalFood);
            
            analysis.append(String.format("Original Food: %s (%.0fg)\n", originalFood, quantity));
            analysis.append(String.format("Calories: %.1f kcal\n", origCalories));
            analysis.append(String.format("Protein: %.1fg, Carbs: %.1fg, Fat: %.1fg\n\n", 
                    origNutrients.getProteinPerGram() * quantity,
                    origNutrients.getCarbsPerGram() * quantity,
                    origNutrients.getFatPerGram() * quantity));
            
            for (int i = 0; i < Math.min(suggestions.size(), 3); i++) {
                String suggestedFood = suggestions.get(i).split(" \\(")[0].trim();
                try {
                    double sugCalories = nutritionDao.getNutrientInfo(suggestedFood).getCaloriesPerGram() * quantity;
                    NutrientInfo sugNutrients = nutritionDao.getNutrientInfo(suggestedFood);
                    
                    analysis.append(String.format("Option %d: %s\n", i+1, suggestedFood));
                    analysis.append(String.format("Calorie Change: %+.1f kcal (%.1f%% change)\n", 
                            sugCalories - origCalories,
                            ((sugCalories - origCalories) / origCalories) * 100));
                    analysis.append(String.format("Protein Change: %+.1fg\n", 
                            (sugNutrients.getProteinPerGram() - origNutrients.getProteinPerGram()) * quantity));
                    analysis.append("\n");
                } catch (Exception e) {
                    analysis.append(String.format("Option %d: %s - Nutrition data unavailable\n\n", i+1, suggestedFood));
                }
            }
            
            analysisText.setText(analysis.toString());
            chartPanel.add(new JScrollPane(analysisText), BorderLayout.CENTER);
            
        } catch (Exception e) {
            chartPanel.add(new JLabel("Error creating impact analysis: " + e.getMessage()));
        }
        
        return chartPanel;
    }

    private void onGetSuggestions(ActionEvent ev) {
        statusLabel.setText("Use individual 'Suggest & Apply Swap' buttons next to each ingredient");
    }

    private void onApplySwap(ActionEvent ev) {
        statusLabel.setText("Use individual 'Suggest & Apply Swap' buttons next to each ingredient");
    }

    
    private void applySingleSwap(String food, double qty, String goal, Meal original, String suggestion) {
        statusLabel.setText("Applying swap: " + food + " → " + suggestion);

        try {
            String newFood = suggestion;

            Meal swappedMeal = swapEngine.applySwap(original, food, newFood);
            
            
            mealDao.insert(swappedMeal);

            statusLabel.setText("Swap applied successfully: " + newFood);

            double originalCalories = nutritionDao.getNutrientInfo(food).getCaloriesPerGram() * qty;
            double newCalories = nutritionDao.getNutrientInfo(newFood).getCaloriesPerGram() * qty;
            double calorieDifference = newCalories - originalCalories;

            String successMessage = String.format(
                "✓ Swap Applied Successfully!\n\n" +
                "Original: %s (%.0fg) = %.1f kcal\n" +
                "New: %s (%.0fg) = %.1f kcal\n" +
                "Calorie Change: %+.1f kcal\n\n" +
                "New meal created with ID: %d",
                food, qty, originalCalories,
                newFood, qty, newCalories,
                calorieDifference,
                swappedMeal.getId()
            );

            JOptionPane.showMessageDialog(this, successMessage, "Swap Applied", JOptionPane.INFORMATION_MESSAGE);

            // Reload meals to show the new swapped meal
            onLoadMeals(null);

        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Database error during swap");
            JOptionPane.showMessageDialog(this,
                    "Database error applying swap:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error during swap operation");
            JOptionPane.showMessageDialog(this,
                    "Error during swap operation:\n" + e.getMessage(),
                    "Swap Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setProfile(int profileId) {
        tfProfileId.setText(String.valueOf(profileId));
        onLoadMeals(null);
    }

    private void onLoadMeals(ActionEvent ev) {
        try {
            int profileId = Integer.parseInt(tfProfileId.getText().trim());
            
            List<Meal> meals = mealDao.findAll();
            List<Meal> filtered = new java.util.ArrayList<>();
            for (Meal meal : meals) {
                if (meal.getProfileId() == profileId) {
                    filtered.add(meal);
                }
            }
            
            cbMeals.removeAllItems();
            for (Meal m : filtered) {
                cbMeals.addItem(m);
            }
            
            if (filtered.size() > 0) {
                statusLabel.setText("Loaded " + filtered.size() + " meals");
                onMealSelected();
            } else {
                statusLabel.setText("No meals found. Use Meal Logger to create meals first.");
            }
            
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid profile ID");
        } catch (SQLException e) {
            statusLabel.setText("Database error");
        }
    }

    private void setupMealRenderer() {
        cbMeals.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Meal) {
                    Meal meal = (Meal) value;
                    String displayText = String.format("ID:%d - %s @ %s (%d ingredients)",
                            meal.getId(),
                            meal.getType(),
                            meal.getLoggedAt().format(dtf),
                            meal.getIngredients().size());
                    setText(displayText);
                }

                return this;
            }
        });
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                MealJournalUI ui = new MealJournalUI();
                ui.setProfile(1); // Auto-load profile 1

                JFrame frame = new JFrame("NutriSci - Meal Journal & Food Swaps");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(true);
                frame.add(ui);
                frame.setSize(700, 500); // Smaller size since we removed nutrition panel
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error starting application: " + e.getMessage(),
                        "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
