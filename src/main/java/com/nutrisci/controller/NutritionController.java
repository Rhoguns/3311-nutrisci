package com.nutrisci.controller;

import com.nutrisci.dao.NutritionDAO;

import java.sql.SQLException;
import java.util.Map;

public class NutritionController {
    // Data Access Object for interacting with nutrition data.
    private final NutritionDAO dao;

    /**
     * Constructs a new NutritionController with the given NutritionDAO.
     *
     * @param dao The Data Access Object for nutrition entities.
     */
    public NutritionController(NutritionDAO dao) {
        this.dao = dao;
    }

    /**
     * Retrieves the calorie content per gram for a specified food item.
     *
     * @param foodName The name of the food item.
     * @return The calories per gram for the food item.
     * @throws SQLException If a database access error occurs.
     */
    public double getCaloriesPerGram(String foodName) throws SQLException {
        return dao.getCaloriesPerGram(foodName);
    }

    /**
     * Retrieves the nutrient breakdown (e.g., protein, carbs, fat) for a specified food item.
     * The breakdown is returned as a Map where keys are nutrient names (String)
     * and values are their respective quantities (Double).
     *
     * @param foodName The name of the food item.
     * @return A Map containing the nutrient breakdown for the food item.
     *         Returns an empty map or a map with default values if the food or nutrient data is not found.
     * @throws SQLException If a database access error occurs.
     */
    public Map<String, Double> getNutrientBreakdown(String foodName) throws SQLException {
        return dao.getNutrientBreakdown(foodName);
    }
}