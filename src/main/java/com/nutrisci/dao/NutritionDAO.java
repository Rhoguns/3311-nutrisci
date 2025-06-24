package main.java.com.nutrisci.dao;

/**
 * Data Access Object for nutrient information.
 */
public interface NutritionDAO {
    /**
     * Returns calories per gram for the given food.
     */
    double getCaloriesPerGram(String foodName);

    /**
     * Returns a map of nutrientName -> gramsPerGramOfFood.
     */
    java.util.Map<String, Double> getNutrientBreakdownPerGram(String foodName);
}
