package com.nutrisci.dao;

import com.nutrisci.connector.DatabaseConnector;
import com.nutrisci.model.Meal;

import java.sql.*;
import java.util.*;

/**
 * JDBC‐backed NutritionDAO against your CNF staging tables and nutrient_data.
 */
public class NutritionDAOImpl implements NutritionDAO {

    /**
     * Maps a user‐entered partial (e.g. "Bread") to the actual
     * nutrient_data.food_name (COALESCE(common_name,long_desc)) via LIKE.
     */
    public String findBestMatch(String partial) throws SQLException {
        String sql = """
            SELECT food_name
              FROM nutrient_data
             WHERE food_name LIKE CONCAT('%', ?, '%')
             LIMIT 1
        """;
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, partial);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("food_name") : null;
            }
        }
    }

    @Override
    public double getCaloriesPerGram(String foodName) throws SQLException {
        // first resolve partial → canonical
        String canonical = findBestMatch(foodName);
        if (canonical == null) return 0.0;

        String sql = "SELECT calories_per_gram FROM nutrient_data WHERE food_name = ?";
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, canonical);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("calories_per_gram") : 0.0;
            }
        }
    }

    @Override
    public Map<String, Double> getNutrientInfo(String foodName) throws SQLException {
        String canonical = findBestMatch(foodName);
        if (canonical == null) return Collections.emptyMap();

        String sql = """
            SELECT protein_per_gram, carbs_per_gram, fat_per_gram, fibre_per_gram
              FROM nutrient_data
             WHERE food_name = ?
        """;
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, canonical);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Collections.emptyMap();
                Map<String, Double> m = new HashMap<>();
                m.put("protein", rs.getDouble("protein_per_gram"));
                m.put("carbs",   rs.getDouble("carbs_per_gram"));
                m.put("fat",     rs.getDouble("fat_per_gram"));
                m.put("fibre",   rs.getDouble("fibre_per_gram")); 
                return m;
            }
        }
    }

    public List<String> findFoodsInGroup(String groupCode) throws SQLException {
        String sql = """
            SELECT COALESCE(common_name, long_desc) as food_name
              FROM cnf_food_name
             WHERE fdgrp_cd = ?
             ORDER BY food_name
        """;
        List<String> foods = new ArrayList<>();
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, groupCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    foods.add(rs.getString("food_name"));
                }
            }
        }
        return foods;
    }

    public String getFoodGroup(String foodName) throws SQLException {
        // resolve partial → canonical
        String canonical = findBestMatch(foodName);
        if (canonical == null) return null;

        String sql = """
            SELECT fdgrp_cd
              FROM cnf_food_name
             WHERE common_name = ?
                OR long_desc   = ?
             LIMIT 1
        """;
        try (Connection c = DatabaseConnector.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, canonical);
            ps.setString(2, canonical);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("fdgrp_cd") : null;
            }
        }
    }

    @Override
    public Map<String, Double> calculateMealNutrients(Map<String, Double> ingredients) throws SQLException {
        Map<String, Double> totalNutrients = new java.util.HashMap<>();
        totalNutrients.put("calories", 0.0);
        totalNutrients.put("protein", 0.0);
        totalNutrients.put("carbs", 0.0);
        totalNutrients.put("fat", 0.0);

        for (Map.Entry<String, Double> entry : ingredients.entrySet()) {
            Map<String, Double> foodNutrients = getNutrientInfo(entry.getKey());
            double quantity = entry.getValue();
            
            for (Map.Entry<String, Double> nutrient : foodNutrients.entrySet()) {
                totalNutrients.compute(nutrient.getKey(), (k, v) -> v + (nutrient.getValue() * quantity / 100.0));
            }
        }
        return totalNutrients;
    }

    public Map<String, Double> getNutrientBreakdown(String foodName) throws SQLException {
        // Your implementation here...
        return getNutrientInfo(foodName); // Example implementation
    }

    @Override
    public Map<String, Double> getNutrientTotalsForMeal(Meal meal) throws SQLException {
        if (meal.getIngredients() == null || meal.getIngredients().isEmpty()) {
            return Collections.emptyMap();
        }
        
        return calculateMealNutrients(meal.getIngredients());
    }
}
