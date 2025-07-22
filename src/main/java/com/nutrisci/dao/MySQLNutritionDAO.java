/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.dao;

import com.nutrisci.connector.DatabaseConnector;
import com.nutrisci.model.Meal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MySQLNutritionDAO
implements NutritionDAO {
    @Override
    public Map<String, Double> getNutrientInfo(String foodName) throws SQLException {
        Map<String, Double> nutrients = new HashMap<>();
        
        String sql = "SELECT nn.nutrient_name, na.nutrient_value " +
                     "FROM food f " +
                     "JOIN nutrient_amount na ON f.id = na.food_id " +
                     "JOIN nutrient_name nn ON na.nutrient_name_id = nn.id " +
                     "WHERE f.food_description = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, foodName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nutrientName = rs.getString("nutrient_name");
                    double nutrientValue = rs.getDouble("nutrient_value");
                    nutrients.put(nutrientName, nutrientValue);
                }
            }
        }
        
        return nutrients;
    }

    @Override
    public double getCaloriesPerGram(String foodName) throws SQLException {
        String sql = "SELECT na.nutrient_value " +
                     "FROM food f " +
                     "JOIN nutrient_amount na ON f.id = na.food_id " +
                     "JOIN nutrient_name nn ON na.nutrient_name_id = nn.id " +
                     "WHERE f.food_description = ? AND nn.nutrient_name LIKE 'Energy%'";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, foodName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("nutrient_value") / 100.0;
                }
            }
        }
        return 0.0;
    }

    @Override
    public Map<String, Double> calculateMealNutrients(Map<String, Double> ingredients) throws SQLException {
        Map<String, Double> totalNutrients = new HashMap<>();
        
        for (Map.Entry<String, Double> ingredient : ingredients.entrySet()) {
            Map<String, Double> nutrientInfo = getNutrientInfo(ingredient.getKey());
            double quantity = ingredient.getValue();
            
            for (Map.Entry<String, Double> nutrient : nutrientInfo.entrySet()) {
                double adjustedValue = nutrient.getValue() * (quantity / 100.0);
                totalNutrients.merge(nutrient.getKey(), adjustedValue, Double::sum);
            }
        }
        
        return totalNutrients;
    }

    @Override
    public Map<String, Double> getNutrientBreakdown(String foodName) throws SQLException {
        return getNutrientInfo(foodName);
    }

    @Override
    public Map<String, Double> getNutrientTotalsForMeal(Meal meal) throws SQLException {
        Map<String, Double> totalNutrients = new HashMap<>();
        if (meal.getIngredients() == null || meal.getIngredients().isEmpty()) {
            return totalNutrients;
        }

        String sql = "SELECT nn.nutrient_name, na.nutrient_value " +
                     "FROM food f " +
                     "JOIN nutrient_amount na ON f.id = na.food_id " +
                     "JOIN nutrient_name nn ON na.nutrient_name_id = nn.id " +
                     "WHERE f.food_description = ? AND nn.nutrient_name IN " +
                     "('Energy (kcal)', 'Protein', 'Carbohydrate, by difference', 'Total lipid (fat)', 'Fibre, total dietary')";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Map.Entry<String, Double> ingredient : meal.getIngredients().entrySet()) {
                String foodName = ingredient.getKey();
                double quantityGrams = ingredient.getValue();

                ps.setString(1, foodName);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String nutrientName = rs.getString("nutrient_name");
                        double nutrientValue = rs.getDouble("nutrient_value") * (quantityGrams / 100.0);
                        totalNutrients.merge(nutrientName, nutrientValue, Double::sum);
                    }
                }
            }
        }
        return totalNutrients;
    }

    @Override
    public String getFoodGroup(String foodName) throws SQLException {
        String sql = "SELECT food_group FROM food WHERE food_description = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, foodName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("food_group");
                }
            }
        }
        return "Unknown";
    }
}
