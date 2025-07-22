/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.dao;

import com.nutrisci.model.Meal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class InMemoryNutritionDAO implements NutritionDAO {
    private final Map<String, Double> caloriesMap;
    private final Map<String, Map<String, Double>> breakdownMap;
    private final Map<String, String> foodGroupMap;

    public InMemoryNutritionDAO() {
        // Use Map.ofEntries() for maps with more than 10 entries
        caloriesMap = new HashMap<>(Map.ofEntries(
            Map.entry("Apple", 0.52),
            Map.entry("Banana", 0.89),
            Map.entry("White Rice", 1.3),
            Map.entry("Brown Rice", 1.1),
            Map.entry("White Bread", 2.65),
            Map.entry("Whole Wheat Bread", 2.47),
            Map.entry("Lettuce Wrap", 0.15),
            Map.entry("Egg", 1.55),
            Map.entry("egg", 1.55), // CRITICAL: Add this for UIComponentTest
            Map.entry("Bread", 2.65),
            Map.entry("Cucumber", 0.15)
        ));

        breakdownMap = new HashMap<>();
        // Add breakdown for all foods used in tests
        breakdownMap.put("Egg", Map.of("calories", 1.55, "protein", 0.13, "fat", 0.11));
        breakdownMap.put("egg", Map.of("calories", 1.55, "protein", 0.13, "fat", 0.11)); // Add lowercase variant
        breakdownMap.put("Bread", Map.of("calories", 2.65, "carbs", 0.49));
        breakdownMap.put("Cucumber", Map.of("calories", 0.15, "carbs", 0.04));
        breakdownMap.put("Apple", Map.of("calories", 0.52, "carbs", 0.14, "fiber", 0.024));
        breakdownMap.put("Banana", Map.of("calories", 0.89, "carbs", 0.23, "fiber", 0.026));
        breakdownMap.put("White Rice", Map.of("calories", 1.3, "carbs", 0.28, "fiber", 0.004));
        breakdownMap.put("Brown Rice", Map.of("calories", 1.1, "carbs", 0.23, "fiber", 0.018));
        breakdownMap.put("White Bread", Map.of("calories", 2.65, "carbs", 0.49, "fiber", 0.027));
        breakdownMap.put("Whole Wheat Bread", Map.of("calories", 2.47, "carbs", 0.41, "fiber", 0.07));
        breakdownMap.put("Lettuce Wrap", Map.of("calories", 0.15, "carbs", 0.029));

        foodGroupMap = new HashMap<>();
        foodGroupMap.put("Apple", "Vegetables and Fruits");
        foodGroupMap.put("Banana", "Vegetables and Fruits");
        foodGroupMap.put("Cucumber", "Vegetables and Fruits");
        foodGroupMap.put("Lettuce Wrap", "Vegetables and Fruits");
        foodGroupMap.put("White Rice", "Grain Products");
        foodGroupMap.put("Brown Rice", "Grain Products");
        foodGroupMap.put("White Bread", "Grain Products");
        foodGroupMap.put("Whole Wheat Bread", "Grain Products");
        foodGroupMap.put("Bread", "Grain Products");
        foodGroupMap.put("Egg", "Meat and Alternatives");
        foodGroupMap.put("egg", "Meat and Alternatives");
    }

    @Override
    public double getCaloriesPerGram(String foodName) throws SQLException {
        return this.caloriesMap.getOrDefault(foodName, 0.0);
    }

    @Override
    public Map<String, Double> getNutrientBreakdown(String foodName) throws SQLException {
        if (!this.breakdownMap.containsKey(foodName)) {
            throw new SQLException("Nutrient breakdown not found for: " + foodName);
        }
        return this.breakdownMap.get(foodName);
    }

    @Override
    public Map<String, Double> calculateMealNutrients(Map<String, Double> ingredients) throws SQLException {
        Map<String, Double> totalNutrients = new HashMap<>();
        totalNutrients.put("calories", 0.0);
        totalNutrients.put("protein", 0.0);
        totalNutrients.put("carbs", 0.0);
        totalNutrients.put("fat", 0.0);
        totalNutrients.put("fibre", 0.0);

        for (Map.Entry<String, Double> ingredientEntry : ingredients.entrySet()) {
            String foodName = ingredientEntry.getKey();
            double quantity = ingredientEntry.getValue();

            // Add calories
            double caloriesPerGram = getCaloriesPerGram(foodName);
            totalNutrients.merge("calories", caloriesPerGram * quantity, Double::sum);

            // Add other nutrients from the breakdown map
            Map<String, Double> breakdown = getNutrientBreakdown(foodName);
            for (Map.Entry<String, Double> nutrientEntry : breakdown.entrySet()) {
                String nutrientName = nutrientEntry.getKey();
                double nutrientValuePerGram = nutrientEntry.getValue();
                // The breakdown is already per gram, so just multiply by quantity
                totalNutrients.merge(nutrientName, nutrientValuePerGram * quantity, Double::sum);
            }
        }
        return totalNutrients;
    }

    @Override
    public String getFoodGroup(String foodName) throws SQLException {
        return foodGroupMap.getOrDefault(foodName, "Unknown");
    }

    @Override
    public Map<String, Double> getNutrientInfo(String foodName) throws SQLException {
        return getNutrientBreakdown(foodName);
    }

    @Override
    public Map<String, Double> getNutrientTotalsForMeal(Meal meal) throws SQLException {
        return calculateMealNutrients(meal.getIngredients());
    }
}
