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
            setStatus("No meal selected.");
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

        setStatus("Meal selected: " + selectedMeal.getType() + " - " + selectedMeal.getIngredients().size() + " ingredients");
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
        setStatus("Getting suggestions for " + ingredient + "...");

        try {
            String suggestion = swapEngine.suggestSwap(ingredient, selectedGoal);

            if (suggestion == null || suggestion.equals("No suggestion available")) {
                setStatus("No swaps available for " + ingredient);

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

            List<String> suggestions = List.of(suggestion);

            showNutritionalSwapAnalysis(ingredient, quantity, suggestions, selectedGoal, originalMeal);

        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Error during swap operation");
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

    // ... (rest of the code remains unchanged, except all statusLabel.setText() are now setStatus())

    private JPanel createNutritionComparisonPanel(String originalFood, double quantity, List<String> suggestions) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        try {
            double origCalories = nutritionDao.getNutrientInfo(originalFood).getCaloriesPerGram() * quantity;
            NutrientInfo origNutrients = nutritionDao.getNutrientInfo(originalFood);
            double origProtein = origNutrients.getProteinPerGram() * quantity;
            double origCarbs = origNutrients.getCarbsPerGram() * quantity;
            double origFat = origNutrients.getFatPerGram() * quantity;
            double origFiber = 0.0; // Fiber data not available

            JPanel originalPanel = createFoodNutritionPanel(
                    String.format("ORIGINAL: %s (%.0fg)", originalFood, quantity),
                    origCalories, origProtein, origCarbs, origFat, origFiber,
                    Color.LIGHT_GRAY
            );
            panel.add(originalPanel);
            panel.add(Box.createVerticalStrut(10));

            for (int i = 0; i < Math.min(suggestions.size(), 5); i++) {
                String suggestion = suggestions.get(i);
                String suggestedFood = suggestion.split(" \\(")[0].trim();

                try {
                    double sugCalories = nutritionDao.getNutrientInfo(suggestedFood).getCaloriesPerGram() * quantity;
                    NutrientInfo sugNutrients = nutritionDao.getNutrientInfo(suggestedFood);
                    double sugProtein = sugNutrients.getProteinPerGram() * quantity;
                    double sugCarbs = sugNutrients.getCarbsPerGram() * quantity;
                    double sugFat = sugNutrients.getFatPerGram() * quantity;
                    double sugFiber = 0.0;

                    double calDelta = sugCalories - origCalories;
                    double proteinDelta = sugProtein - origProtein;
                    double carbsDelta = sugCarbs - origCarbs;
                    double fatDelta = sugFat - origFat;
                    double fiberDelta = sugFiber - origFiber;

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

    // ... other unchanged methods ...

    // REFACTOR: Centralized status update method
    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private void onGetSuggestions(ActionEvent ev) {
        setStatus("Use individual 'Suggest & Apply Swap' buttons next to each ingredient");
    }

    private void onApplySwap(ActionEvent ev) {
        setStatus("Use individual 'Suggest & Apply Swap' buttons next to each ingredient");
    }

    private void applySingleSwap(String food, double qty, String goal, Meal original, String suggestion) {
        setStatus("Applying swap: " + food + " → " + suggestion);

        try {
            String newFood = suggestion;
            Meal swappedMeal = swapEngine.applySwap(original, food, newFood);
            mealDao.insert(swappedMeal);

            setStatus("Swap applied successfully: " + newFood);

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
            onLoadMeals(null);

        } catch (SQLException e) {
            e.printStackTrace();
            setStatus("Database error during swap");
            JOptionPane.showMessageDialog(this,
                    "Database error applying swap:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Error during swap operation");
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
                setStatus("Loaded " + filtered.size() + " meals");
                onMealSelected();
            } else {
                setStatus("No meals found. Use Meal Logger to create meals first.");
            }

        } catch (NumberFormatException e) {
            setStatus("Invalid profile ID");
        } catch (SQLException e) {
            setStatus("Database error");
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
                frame.setSize(700, 500);
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

// Examples (spread throughout many methods):
statusLabel.setText("Meal selected: " + selectedMeal.getType());
statusLabel.setText("Getting suggestions for " + ingredient + "...");
statusLabel.setText("Database error during swap");
// ...and more (over 10+ scattered occurrences)



    
}
