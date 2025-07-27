package com.nutrisci.dao;

import com.nutrisci.connector.DatabaseConnector;
import com.nutrisci.info.NutrientInfo;
import com.nutrisci.model.Meal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MySQLNutritionDAO
implements NutritionDAO {
    @Override
    public NutrientInfo getNutrientInfo(String foodName) throws SQLException {
        Map<String, Object> nutrients = new HashMap<>();
        
        String sql = "SELECT cnn.NutrientName, cna.NutrientValue " +
                     "FROM cnf_food_name cfn " +
                     "JOIN cnf_nutrient_amount cna ON cfn.FoodID = cna.FoodID " +
                     "JOIN cnf_nutrient_name cnn ON cna.NutrientID = cnn.NutrientID " +
                     "WHERE cfn.FoodDescription LIKE CONCAT('%', ?, '%')";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, foodName);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nutrientName = rs.getString("NutrientName");
                    double nutrientValue = rs.getDouble("NutrientValue");
                    nutrients.put(nutrientName, nutrientValue);
                }
            }
        }
        
        NutrientInfo info = new NutrientInfo();
        
        // Convert nutrients map values and handle missing data
        Double calories = (Double) nutrients.get("ENERGY (KILOCALORIES)");
        info.setCaloriesPerGram(calories != null ? calories / 100.0 : 0.0);
        
        Double protein = (Double) nutrients.get("PROTEIN");
        info.setProteinPerGram(protein != null ? protein / 100.0 : 0.0);
        
        Double fat = (Double) nutrients.get("FAT (TOTAL LIPIDS)");
        info.setFatPerGram(fat != null ? fat / 100.0 : 0.0);
        
        // Try different carb field names
        Double carbs = (Double) nutrients.get("CARBOHYDRATE, TOTAL (BY DIFFERENCE)");
        if (carbs == null) {
            carbs = (Double) nutrients.get("CARBOHYDRATE, TOTAL");
            if (carbs == null) {
                carbs = (Double) nutrients.get("CARBOHYDRATE");
            }
        }
        info.setCarbsPerGram(carbs != null ? carbs / 100.0 : 0.0);
        
        return info;
    }

    @Override
    public double getCaloriesPerGram(String foodName) throws SQLException {
        String sql = "SELECT cna.NutrientValue " +
                     "FROM cnf_food_name cfn " +
                     "JOIN cnf_nutrient_amount cna ON cfn.FoodID = cna.FoodID " +
                     "WHERE cfn.FoodDescription LIKE CONCAT('%', ?, '%') " +
                     "AND cna.NutrientID = 208 " +
                     "LIMIT 1";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, foodName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("NutrientValue") / 100.0;
                }
            }
        }
        return 2.5; // Default fallback
    }

    public String getFoodGroup(String foodName) throws SQLException {
        String sql = "SELECT cfg.FoodGroupName " +
                     "FROM cnf_food_name cfn " +
                     "JOIN cnf_food_group cfg ON cfn.FoodGroupID = cfg.FoodGroupID " +
                     "WHERE cfn.FoodDescription LIKE CONCAT('%', ?, '%') " +
                     "LIMIT 1";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, foodName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("FoodGroupName");
                }
            }
        }
        return "Unknown";
    }

    public Map<String, Double> calculateMealNutrients(Map<String, Double> ingredients) throws SQLException {
        Map<String, Double> totalNutrients = new HashMap<>();
        totalNutrients.put("calories", 0.0);
        totalNutrients.put("protein", 0.0);
        totalNutrients.put("fat", 0.0);
        totalNutrients.put("carbs", 0.0);
        
        for (Map.Entry<String, Double> ingredient : ingredients.entrySet()) {
            NutrientInfo nutrientInfo = getNutrientInfo(ingredient.getKey());
            double quantity = ingredient.getValue();
            
            totalNutrients.put("calories", totalNutrients.get("calories") + (nutrientInfo.getCaloriesPerGram() * quantity));
            totalNutrients.put("protein", totalNutrients.get("protein") + (nutrientInfo.getProteinPerGram() * quantity));
            totalNutrients.put("fat", totalNutrients.get("fat") + (nutrientInfo.getFatPerGram() * quantity));
            totalNutrients.put("carbs", totalNutrients.get("carbs") + (nutrientInfo.getCarbsPerGram() * quantity));
        }
        
        return totalNutrients;
    }

    public Map<String, Double> getNutrientBreakdown(String foodName) throws SQLException {
        NutrientInfo info = getNutrientInfo(foodName);
        
        Map<String, Double> breakdown = new HashMap<>();
        breakdown.put("calories", info.getCaloriesPerGram());
        breakdown.put("protein", info.getProteinPerGram());
        breakdown.put("fat", info.getFatPerGram());
        breakdown.put("carbs", info.getCarbsPerGram());
        
        return breakdown;
    }

    public Map<String, Double> getNutrientTotalsForMeal(Meal meal) throws SQLException {
        if (meal.getIngredients() == null || meal.getIngredients().isEmpty()) {
            return new HashMap<>();
        }
        return calculateMealNutrients(meal.getIngredients());
    }
}
