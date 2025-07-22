
package com.nutrisci.controller;

import com.nutrisci.dao.NutritionDAO;
import java.sql.SQLException;
import java.util.Map;

public class NutritionController {
    private final NutritionDAO dao;

    public NutritionController(NutritionDAO dao) {
        this.dao = dao;
    }

    public double getCaloriesPerGram(String foodName) throws SQLException {
        return this.dao.getCaloriesPerGram(foodName);
    }

    public Map<String, Double> getNutrientBreakdown(String foodName) throws SQLException {
        return this.dao.getNutrientBreakdown(foodName);
    }

    public double getFoodCalories(String foodName) throws SQLException {
        // Use the correct method from the updated DAO interface
        return this.dao.getCaloriesPerGram(foodName) * 100.0; // Example: return per 100g
    }
}
