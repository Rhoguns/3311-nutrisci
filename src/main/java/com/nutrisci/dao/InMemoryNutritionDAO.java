package com.nutrisci.dao;

import com.nutrisci.info.NutrientInfo;
import com.nutrisci.model.Meal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class InMemoryNutritionDAO implements NutritionDAO {
    private Map<String, Double> caloriesMap;
    private Map<String, Map<String, Double>> breakdownMap;
    private Map<String, String> foodGroupMap;

    public InMemoryNutritionDAO() {
        caloriesMap = new HashMap<>(Map.ofEntries(
            Map.entry("Apple", 0.52),
            Map.entry("Banana", 0.89),
            Map.entry("White Rice", 1.3),
            Map.entry("Brown Rice", 1.1),
            Map.entry("White Bread", 2.65),
            Map.entry("Whole Wheat Bread", 2.47),
            Map.entry("Lettuce Wrap", 0.15),
            Map.entry("Egg", 1.55),
            Map.entry("egg", 1.55), 
            Map.entry("Bread", 2.65),
            Map.entry("Cucumber", 0.15)
        ));

        breakdownMap = new HashMap<>();
        breakdownMap.put("Egg", Map.of("calories", 1.55, "protein", 0.13, "carbs", 0.01, "fat", 0.11));
        breakdownMap.put("egg", Map.of("calories", 1.55, "protein", 0.13, "carbs", 0.01, "fat", 0.11));
        breakdownMap.put("Bread", Map.of("calories", 2.65, "protein", 0.09, "carbs", 0.49, "fat", 0.03));
        breakdownMap.put("Cucumber", Map.of("calories", 0.15, "protein", 0.007, "carbs", 0.04, "fat", 0.001));
        breakdownMap.put("Apple", Map.of("calories", 0.52, "protein", 0.003, "carbs", 0.14, "fat", 0.002));
        breakdownMap.put("Banana", Map.of("calories", 0.89, "protein", 0.011, "carbs", 0.23, "fat", 0.003));
        breakdownMap.put("White Rice", Map.of("calories", 1.3, "protein", 0.027, "carbs", 0.28, "fat", 0.003));
        breakdownMap.put("Brown Rice", Map.of("calories", 1.1, "protein", 0.026, "carbs", 0.23, "fat", 0.009));
        breakdownMap.put("White Bread", Map.of("calories", 2.65, "protein", 0.09, "carbs", 0.49, "fat", 0.032));
        breakdownMap.put("Whole Wheat Bread", Map.of("calories", 2.47, "protein", 0.13, "carbs", 0.41, "fat", 0.038));
        breakdownMap.put("Lettuce Wrap", Map.of("calories", 0.15, "protein", 0.014, "carbs", 0.029, "fat", 0.002));

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
    public double getCaloriesPerGram(String foodName) {
        Map<String, Double> breakdown = breakdownMap.get(foodName);
        if (breakdown == null) return 0.0;
        return breakdown.getOrDefault("calories", 0.0);
    }


    public Map<String, Double> getNutrientBreakdown(String foodName) {
        return breakdownMap.getOrDefault(foodName, Map.of(
            "calories", 0.0,
            "protein", 0.0,
            "carbs", 0.0,
            "fat", 0.0,
            "fibre", 0.0
        ));
    }


    public Map<String, Double> calculateMealNutrients(Map<String, Double> ingredients) {
        Map<String, Double> totalNutrients = new HashMap<>();
        for (Map.Entry<String, Double> entry : ingredients.entrySet()) {
            String foodName = entry.getKey();
            double quantity = entry.getValue();
            Map<String, Double> breakdown = getNutrientBreakdown(foodName);
            for (Map.Entry<String, Double> nutrient : breakdown.entrySet()) {
                totalNutrients.merge(nutrient.getKey(), nutrient.getValue() * quantity, Double::sum);
            }
        }
        return totalNutrients;
    }


    public String getFoodGroup(String foodName) {
        return foodGroupMap.getOrDefault(foodName, "Unknown");
    }


    @Override
    public NutrientInfo getNutrientInfo(String foodName) {
        Map<String, Double> breakdown = getNutrientBreakdown(foodName);
        NutrientInfo info = new NutrientInfo();
        info.setCaloriesPerGram(breakdown.getOrDefault("calories", 0.0));
        info.setProteinPerGram(breakdown.getOrDefault("protein", 0.0));
        info.setFatPerGram(breakdown.getOrDefault("fat", 0.0));
        info.setCarbsPerGram(breakdown.getOrDefault("carbs", 0.0));
        return info;
    }

    public Map<String, Double> getNutrientTotalsForMeal(Meal meal) throws SQLException {
        return calculateMealNutrients(meal.getIngredients());
    }
}
