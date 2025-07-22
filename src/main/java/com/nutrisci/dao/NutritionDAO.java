/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.dao;

import com.nutrisci.model.Meal;
import java.sql.SQLException;
import java.util.Map;

public interface NutritionDAO {
    /**
     * Calculates nutrient totals for a meal based on its ingredients.
     */
    Map<String, Double> calculateMealNutrients(Map<String, Double> ingredients) throws SQLException;

    /**
     * Gets nutrient totals for a specific meal.
     */
    Map<String, Double> getNutrientTotalsForMeal(Meal meal) throws SQLException;

    /**
     * Gets calories per gram for a specific food item.
     */
    double getCaloriesPerGram(String foodName) throws SQLException;

    /**
     * Gets nutrient breakdown for a food item.
     */
    Map<String, Double> getNutrientBreakdown(String foodName) throws SQLException;

    /**
     * Gets all nutrient information for a food item.
     */
    Map<String, Double> getNutrientInfo(String foodName) throws SQLException;

    /**
     * Gets the food group for a specific food item.
     */
    String getFoodGroup(String foodName) throws SQLException;
}
