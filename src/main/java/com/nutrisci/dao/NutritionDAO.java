package main.java.com.nutrisci.dao;

/**
 * Data Access Object for nutrient information.
 */
public interface NutritionDAO {
    /**
     * Returns the calorie count per gram for a given food item.
     *
     * @param foodName The name of the food item (e.g., "Egg", "Bread").
     * @return The calories per gram for the specified food.
     */
    double getCaloriesPerGram(String foodName);

    /**
     * Returns a map of nutrient breakdown per gram for a given food item.
     *
     * @param foodName The name of the food item.
     * @return A map where keys are nutrient names and values are grams per gram of food.
     */
    java.util.Map<String, Double> getNutrientBreakdownPerGram(String foodName);
}
