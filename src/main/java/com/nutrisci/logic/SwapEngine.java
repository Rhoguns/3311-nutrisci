package com.nutrisci.logic;
import com.nutrisci.model.Meal;
import com.nutrisci.service.AnalysisModule;
import com.nutrisci.dao.NutritionDAO;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for generating food‐swap suggestions based on a nutritional goal,
 * comparing original vs. swapped meals, and applying swaps.
 */
public class SwapEngine {
    private final AnalysisModule analysisModule;
    private final NutritionDAO nutritionDao;

    /**
     * Hardcoded swap options keyed by goal → (originalFood → suggestedSwap).
     */
    private static final Map<String, Map<String, String>> SWAP_OPTIONS = Map.of(
        "reduce calories", Map.of(
            "Bread",      "Lettuce wrap",
            "White Rice", "Cauliflower rice"
        ),
        "increase fiber", Map.of(
            "White Bread",       "Whole wheat bread",
            "Rice",              "Brown rice"
        )
    );

    public SwapEngine(AnalysisModule analysisModule, NutritionDAO nutritionDao) {
        this.analysisModule = analysisModule;
        this.nutritionDao     = nutritionDao;
    }

    /**
     * Suggests a single best‐swap for the given food and goal.
     * @param food the original ingredient name
     * @param goal nutritional goal, e.g. "reduce calories" or "increase fiber"
     * @return the suggested replacement, or a default message if none found
     */
    public String suggestSwap(String food, String goal) {
        return SWAP_OPTIONS
            .getOrDefault(goal.toLowerCase(), Collections.emptyMap())
            .getOrDefault(food, "No suggestion available");
    }

    /**
     * Compares total calories between the original and swapped meal.
     * @param original the original Meal
     * @param swapped the swapped‐ingredient Meal
     * @return (swappedCalories − originalCalories)
     */
    public double compareCalories(Meal original, Meal swapped) throws SQLException {
        double origCal = analysisModule.computeTotalCalories(List.of(original));
        double swapCal = analysisModule.computeTotalCalories(List.of(swapped));
        return swapCal - origCal;
    }

    /**
     * Applies a single‐ingredient swap to a meal, returning a new Meal instance.
     * @param original the original Meal to copy
     * @param oldFood the ingredient name to remove
     * @param newFood the ingredient name to add
     * @param newQty quantity (grams) for the new ingredient
     * @return a new Meal with the swap applied
     */
    public Meal applySwap(Meal original, String oldFood, String newFood, double newQty) {
        Meal copy = new Meal(original.getType(), original.getLoggedAt());
        copy.setProfileId(original.getProfileId());
        // Iterate through the original meal's ingredients.
        // copy over all other ingredients
        original.getIngredients().forEach((name, qty) -> {
            if (!name.equals(oldFood)) {
                copy.getIngredients().put(name, qty);
            }
        });
        // add the new ingredient
        copy.getIngredients().put(newFood, newQty);
        return copy;
    }
}