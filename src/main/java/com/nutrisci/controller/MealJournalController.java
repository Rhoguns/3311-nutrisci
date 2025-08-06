package com.nutrisci.controller;

import com.nutrisci.dao.MealDAO;
import com.nutrisci.dao.NutritionDAO;
import com.nutrisci.logic.SwapEngine;
import com.nutrisci.model.Meal;
import com.nutrisci.model.NutrientTotals; import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
public class MealJournalController {
    private static final Logger logger = Logger.getLogger(MealJournalController.class.getName());
    private final MealDAO mealDao;
    private final NutritionController nutritionController;
    private final SwapEngine swapEngine;
    
    public MealJournalController(MealDAO mealDao, NutritionDAO nutritionDao, SwapEngine swapEngine) {
        this.mealDao = mealDao;
        this.nutritionController = new NutritionController(nutritionDao);
        this.swapEngine = swapEngine;
    }
    
    
      public String suggestAndAnalyzeSwap(String ingredient, double quantity, String goal) throws SQLException {
        String suggestion = swapEngine.suggestSwap(ingredient, goal);
        
        if (suggestion == null || suggestion.equals("No suggestion available")) {
            return "No suggestion available for " + ingredient + " with goal: " + goal;
        }
        
        try {
            double originalCalories = nutritionController.getCaloriesPerGram(ingredient) * quantity;
            double newCalories = nutritionController.getCaloriesPerGram(suggestion) * quantity;
            double calorieDifference = newCalories - originalCalories;
            
            if (Math.abs(calorieDifference) < 10) {
                return String.format("Minimal impact swap: %s → %s (%.1f cal difference)", 
                                   ingredient, suggestion, calorieDifference);
            }
            
            return String.format("Swap suggestion: %s → %s (%.1f → %.1f calories)", 
                               ingredient, suggestion, originalCalories, newCalories);
        } catch (SQLException e) {
            return "Error calculating nutrition for swap: " + e.getMessage();
        }
    }
    
    public List<Meal> loadMealsForProfile(int profileId) throws SQLException {
        if (profileId <= 0) {
            throw new IllegalArgumentException("Invalid profile ID");
        }
        return mealDao.findByProfileId(profileId);
    }
    
    public Meal applySwap(Meal originalMeal, String originalFood, String newFood) throws SQLException {
        Meal swappedMeal = swapEngine.applySwap(originalMeal, originalFood, newFood);
        mealDao.insert(swappedMeal);
        return swappedMeal;
    }
    
    public NutrientTotals calculateMealNutrition(Meal meal) throws SQLException {
        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        
        for (Map.Entry<String, Double> ingredient : meal.getIngredients().entrySet()) {
            String food = ingredient.getKey();
            double quantity = ingredient.getValue();
            
            try {
                Map<String, Double> nutrients = nutritionController.getNutrientBreakdown(food);
                double calories = nutritionController.getCaloriesPerGram(food) * quantity;
                
                totalCalories += calories;
                totalProtein += nutrients.getOrDefault("protein", 0.0) * quantity;
                totalCarbs += nutrients.getOrDefault("carbs", 0.0) * quantity;
                totalFat += nutrients.getOrDefault("fat", 0.0) * quantity;
            } catch (SQLException e) {
                logger.severe("Error calculating nutrition for " + food + ": " + e.getMessage());
            }
        }

        NutrientTotals totals = new NutrientTotals();
        totals.setCalories(totalCalories);
        totals.setProtein(totalProtein);
        totals.setCarbs(totalCarbs);
        totals.setFat(totalFat);
        return totals;
    }
    
    public boolean validateMeal(Meal meal) {
        return meal != null && 
               meal.getIngredients() != null && 
               !meal.getIngredients().isEmpty();
    }
    
   
    public Map<String, Object> getDetailedSwapAnalysis(String ingredient, double quantity, String goal) throws SQLException {
        Map<String, Object> analysis = new HashMap<>();
        
        String suggestion = swapEngine.suggestSwap(ingredient, goal);
        analysis.put("suggestion", suggestion);
        analysis.put("ingredient", ingredient);
        analysis.put("quantity", quantity);
        analysis.put("goal", goal);
        
        if (suggestion != null && !suggestion.equals("No suggestion available")) {
            try {
                double originalCalories = nutritionController.getCaloriesPerGram(ingredient) * quantity;
                double newCalories = nutritionController.getCaloriesPerGram(suggestion) * quantity;
                
                analysis.put("originalCalories", originalCalories);
                analysis.put("newCalories", newCalories);
                analysis.put("calorieDifference", newCalories - originalCalories);
                analysis.put("hasMinimalImpact", Math.abs(newCalories - originalCalories) < 10);
                
            } catch (SQLException e) {
                analysis.put("error", "Could not calculate nutritional impact: " + e.getMessage());
            }
        }
        
        return analysis;
    }
}
