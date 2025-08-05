package com.nutrisci.dao;

import com.nutrisci.info.NutrientInfo;
import com.nutrisci.model.Meal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class InMemoryNutritionDAO implements NutritionDAO {
    private Map<String, Map<String, Double>> breakdownMap;
    private Map<String, String> foodGroupMap;

    public InMemoryNutritionDAO() {
    	initializeNutritionData();

        foodGroupMap = new HashMap<>();
        foodGroupMap.put("Apple", "Vegetables and Fruits");
        foodGroupMap.put("Banana", "Vegetables and Fruits");
        foodGroupMap.put("Cucumber", "Vegetables and Fruits");
        foodGroupMap.put("Lettuce Wrap", "Vegetables and Fruits");
        foodGroupMap.put("White Rice", "Grain Products");
        foodGroupMap.put("Brown Rice", "Grain Products");
        foodGroupMap.put("White Bread", "Grain Products");
        foodGroupMap.put("Whole Wheat Bread", "Grain Products");
        foodGroupMap.put("Egg", "Meat and Alternatives");
    }
    
    private void initializeNutritionData() {
    	breakdownMap = new HashMap<>();
    	
    	//Vegetables and Fruits
    	addNutritionData("Apple", 0.52, 0.003, 0.14, 0.002);
    	addNutritionData("Banana", 0.89, 0.011, 0.23, 0.003);
    	addNutritionData("Cucumber", 0.15, 0.007, 0.04, 0.001);
    	addNutritionData("Lettuce Wrap", 0.15, 0.014, 0.029, 0.002);
        
    	//Grain Products
    	addNutritionData("Brown Rice", 1.1, 0.026, 0.23, 0.009);
    	addNutritionData("White Bread", 2.65, 0.09, 0.49, 0.032);
    	addNutritionData("White Rice", 1.3, 0.027, 0.28, 0.003);
    	addNutritionData("Whole Wheat Bread", 2.47, 0.13, 0.41, 0.038);
    	
    	//Meat and Alternatives
    	addNutritionData("Egg", 1.55, 0.13, 0.01, 0.11);

    }
    
    private void addNutritionData(String foodName, double calories, double protein, double carbs, double fat){
    	breakdownMap.put(foodName, Map.of(
    			"calories", calories,
    			"protein", protein,
    			"carbs", carbs,
    			"fat", fat
    	));
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
