package com.nutrisci.dao;

import java.sql.SQLException;
import java.util.Map;

public interface NutritionDAO {

    /**
     * Retrieves the calories per gram for a specified food item.
     *
     * @param foodName The name of the food item to query.
     * @return The calories per gram for the food item, or 0.0 if the food is not found.
     * @throws SQLException If a database access error occurs.
     */
    double getCaloriesPerGram(String foodName) throws SQLException;

    /**
     * Retrieves a detailed nutrient breakdown per gram for a specified food item.
     *
     * @param foodName The name of the food item to query.
     * @return A Map where keys are nutrient names (e.g., "protein", "carbs", "fat", "fibre") and values are their quantities per gram. Returns an empty map if the food is not found.
     * @throws SQLException If a database access error occurs.
     */
    Map<String, Double> getNutrientBreakdown(String foodName) throws SQLException;
}