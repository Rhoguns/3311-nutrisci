package main.java.com.nutrisci.dao;

import java.util.HashMap;
import java.util.Map;


/**
 * In-memory implementation of NutritionDAO for retrieving nutritional information.
 * This class uses a hardcoded cache to simulate a database of food items and their
 * calorie and nutrient breakdowns.
 */
public class InMemoryNutritionDAO implements NutritionDAO {
    /**
     * Hardcoded cache of calorie values per gram for various food items.
     * Keys are food names (lowercase), values are calories per gram.
     */
    private static final Map<String, Double> CAL_CACHE = new HashMap<>();
    static {
        CAL_CACHE.put("egg", 1.55);
        CAL_CACHE.put("bread", 2.5);
        CAL_CACHE.put("tomato", 0.18);
    }

    /**
     * Returns the calorie count per gram for a given food item.
     * Falls back to a default rate of 1.0 kcal/gram if the food is unrecognized.
     *
     * @param foodName The name of the food item (e.g., "Egg", "Bread").
     * @return The calories per gram for the specified food.
     */
    @Override
    public double getCaloriesPerGram(String foodName) {
        return CAL_CACHE.getOrDefault(foodName.toLowerCase(), 1.0);
    }

    /**
     * Returns a map of nutrient breakdown per gram for a given food item.
     * Currently, this is a stub implementation that only returns calories.
     *
     * @param foodName The name of the food item.
     * @return A map where keys are nutrient names and values are grams per gram of food.
     */
    @Override
    public Map<String, Double> getNutrientBreakdownPerGram(String foodName) {
        // stub: return calories only
        Map<String, Double> map = new HashMap<>();
        map.put("calories", getCaloriesPerGram(foodName));
        return map;
    }
}
