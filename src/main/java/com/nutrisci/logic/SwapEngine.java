package com.nutrisci.logic;

import com.nutrisci.connector.DatabaseConnector;
import com.nutrisci.dao.MealDAO;
import com.nutrisci.dao.SwapRuleDAO;
import com.nutrisci.model.Meal;
import com.nutrisci.model.SwapRule;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Handles food swaps.
 */
public class SwapEngine {
    private SwapRuleDAO swapRuleDao;
    private MealDAO mealDao;

    public SwapEngine(SwapRuleDAO swapRuleDao, MealDAO mealDao) {
        this.swapRuleDao = swapRuleDao;
        this.mealDao = mealDao;
    }
    
    public String suggestSwap(String food, String goal) {
        try {
            List<SwapRule> rules = swapRuleDao.findAll();
            
            for (SwapRule rule : rules) {
                if (rule.getOriginalFood().equalsIgnoreCase(food) && 
                    rule.getGoal().equalsIgnoreCase(goal)) {
                    return rule.getSuggestedFood();
                }
            }
        } catch (SQLException e) {
            return "Database error"; 
        }
        return "No swap found"; 
    }

    public Meal applySwap(Meal original, String oldFood, String newFood) {
        Meal swapped = new Meal(original.getType(), original.getLoggedAt());
        swapped.setProfileId(original.getProfileId());

        for (Map.Entry<String, Double> entry : original.getIngredients().entrySet()) {
            String ingredient = entry.getKey().equalsIgnoreCase(oldFood) ? newFood : entry.getKey();
            swapped.getIngredients().put(ingredient, entry.getValue());
        }
        return swapped;
    }

    /**
     * Applies swap rule to meals in date range.
     */
    public int applySwapRuleOverTime(int profileId, int swapRuleId, LocalDate startDate, LocalDate endDate) throws Exception {
        SwapRule rule = swapRuleDao.findById(swapRuleId);
        if (rule == null) {
            throw new Exception("Swap rule " + swapRuleId + " not found");
        }

        List<Meal> meals = mealDao.findByProfileIdAndDateRange(profileId, startDate, endDate);
        int count = 0;
        
        try (Connection conn = DatabaseConnector.getConnection()) {
            conn.setAutoCommit(false);

            for (Meal meal : meals) {
                String foodKey = null;
                for (String key : meal.getIngredients().keySet()) {
                    if (key.equalsIgnoreCase(rule.getOriginalFood())) {
                        foodKey = key;
                        break;
                    }
                }

                if (foodKey != null) {
                    Meal swapped = applySwap(meal, foodKey, rule.getSuggestedFood());
                    mealDao.insert(swapped, conn);
                    mealDao.delete(meal.getId(), conn);
                    count++;
                }
            }
            conn.commit();
            return count;
        } catch (SQLException e) {
            throw new Exception("Database error during swap", e);
        }
    }


    public void saveSwap(Meal original, String oldFood, String newFood) throws Exception {
        Meal swapped = applySwap(original, oldFood, newFood);
        mealDao.insert(swapped); 
    }
}