package com.nutrisci.logic;

import com.nutrisci.connector.DatabaseConnector;
import com.nutrisci.dao.MealDAO; // Import MealDAO
import com.nutrisci.dao.NutritionDAO;
import com.nutrisci.dao.SwapRuleDAO;
import com.nutrisci.model.Meal;
import com.nutrisci.model.SwapRule;
import com.nutrisci.service.AnalysisModule;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SwapEngine {
    private final AnalysisModule analysisModule;
    private final NutritionDAO nutritionDao;
    private final SwapRuleDAO swapRuleDao;
    private final MealDAO mealDao; // Add MealDAO field

    // Updated constructor to accept the MealDAO
    public SwapEngine(AnalysisModule analysisModule, NutritionDAO nutritionDao, SwapRuleDAO swapRuleDao, MealDAO mealDao) {
        this.analysisModule = analysisModule;
        this.nutritionDao = nutritionDao;
        this.swapRuleDao = swapRuleDao;
        this.mealDao = mealDao; // Assign it
    }

    // This method now queries the database
    public String suggestSwap(String food, String goal) {
        try {
            List<SwapRule> allRules = swapRuleDao.findAll();
            
            // Find the best swap from the database rules
            for (SwapRule rule : allRules) {
                if (rule.getOriginalFood().equalsIgnoreCase(food) && rule.getGoal().equalsIgnoreCase(goal)) {
                    return rule.getSuggestedFood(); // Return the first match
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback or error handling
        }
        return "No suggestion available"; // Default if no rule is found
    }

    public Meal applySwap(Meal original, String oldFood, String newFood) {
        // Create the new meal using the original's type and time
        Meal swappedMeal = new Meal(original.getType(), original.getLoggedAt());
        
        // FIX: Copy the profile ID from the original meal to the new one
        swappedMeal.setProfileId(original.getProfileId());
        
        // Copy all ingredients, replacing the old one with the new one
        for (Map.Entry<String, Double> entry : original.getIngredients().entrySet()) {
            if (entry.getKey().equalsIgnoreCase(oldFood)) {
                swappedMeal.getIngredients().put(newFood, entry.getValue());
            } else {
                swappedMeal.getIngredients().put(entry.getKey(), entry.getValue());
            }
        }
        return swappedMeal;
    }

    /**
     * Applies a specific swap rule to all meals for a given profile within a date range.
     * This method finds all meals containing the rule's original food and replaces it
     * with the suggested food, creating new meal records.
     *
     * @param profileId  The ID of the user's profile.
     * @param swapRuleId The ID of the swap rule to apply.
     * @param startDate  The start of the date range (inclusive).
     * @param endDate    The end of the date range (inclusive).
     * @return The number of meals that were successfully swapped.
     * @throws Exception if the swap rule is not found or a database error occurs.
     */
    public int applySwapRuleOverTime(int profileId, int swapRuleId, LocalDate startDate, LocalDate endDate) throws Exception {
        SwapRule rule = swapRuleDao.findById(swapRuleId);
        if (rule == null) {
            throw new Exception("Swap Rule with ID " + swapRuleId + " not found.");
        }
        String originalFood = rule.getOriginalFood();
        String suggestedFood = rule.getSuggestedFood();

        List<Meal> mealsToProcess = mealDao.findByProfileIdAndDateRange(profileId, startDate, endDate);
        
        int updatedCount = 0;
        Connection conn = null;
        try {
            // FIX: Manage the connection and transaction manually
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Start transaction

            for (Meal meal : mealsToProcess) {
                // FIX: Perform a case-insensitive check for the ingredient
                String foodKeyToSwap = meal.getIngredients().keySet().stream()
                    .filter(key -> key.equalsIgnoreCase(originalFood))
                    .findFirst()
                    .orElse(null);

                if (foodKeyToSwap != null) {
                    Meal swappedMeal = applySwap(meal, foodKeyToSwap, suggestedFood);
                    
                    // The DAO methods must be updated to accept a Connection object
                    mealDao.insert(swappedMeal, conn);
                    mealDao.delete(meal.getId(), conn);
                    updatedCount++;
                }
            }
            conn.commit(); // Commit the transaction if all operations succeed
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Roll back on any error
            }
            throw new Exception("Failed to apply swap over time due to a database error.", e);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // Reset auto-commit
                conn.close(); // Close the connection
            }
        }
        return updatedCount;
    }

    /**
     * This method now uses the DAO to persist the swapped meal
     */
    public void saveSwappedMeal(Meal original, String oldFood, String newFood) throws Exception {
        Meal swappedMeal = applySwap(original, oldFood, newFood);
        // FIX: Use the mealDao to insert the new meal
        mealDao.insert(swappedMeal); 
    }
}