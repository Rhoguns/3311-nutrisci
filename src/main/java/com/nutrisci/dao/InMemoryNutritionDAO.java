package com.nutrisci.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class InMemoryNutritionDAO implements NutritionDAO {
    // calories per gram lookup
    private final Map<String, Double> caloriesMap = Map.of(
        "Apple", 0.5,
        "Banana", 0.89
    );

    // full nutrient breakdown lookup
    private final Map<String, Map<String, Double>> breakdownMap = new HashMap<>();

    public InMemoryNutritionDAO() {
        // Example breakdown for Apple
        Map<String, Double> appleNutrients = new HashMap<>();
        // Protein content per gram for Apple
        appleNutrients.put("protein", 0.003);
        // Carbohydrate content per gram for Apple
        appleNutrients.put("carbs",   0.14);
        // Fat content per gram for Apple
        appleNutrients.put("fat",     0.001);
        // Fiber content per gram for Apple
        appleNutrients.put("fibre",   0.02);
        breakdownMap.put("Apple", appleNutrients);

        // Example breakdown for Banana
        Map<String, Double> bananaNutrients = new HashMap<>();
        bananaNutrients.put("protein", 0.011);
        bananaNutrients.put("carbs",   0.23);
        bananaNutrients.put("fat",     0.003);
        bananaNutrients.put("fibre",   0.025);
        breakdownMap.put("Banana", bananaNutrients);
    }

    @Override
    public double getCaloriesPerGram(String foodName) throws SQLException {
        return caloriesMap.getOrDefault(foodName, 0.0);
    }

    @Override
    public Map<String, Double> getNutrientBreakdown(String foodName) throws SQLException {
        return breakdownMap.getOrDefault(foodName, Map.of());
    }
}