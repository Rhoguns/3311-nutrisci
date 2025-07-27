package com.nutrisci.dao;

import com.nutrisci.connector.DatabaseConnector;
import com.nutrisci.info.NutrientInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NutritionDAOImpl implements NutritionDAO {

    @Override
    @Deprecated
    public double getCaloriesPerGram(String foodName) throws SQLException {
        return getNutrientInfo(foodName).getCaloriesPerGram();
    }

    /**
     * Gets nutrition data for food.
     */
    @Override
    public NutrientInfo getNutrientInfo(String foodName) throws SQLException {
        String sql = "SELECT calories_per_gram, protein_per_gram, fat_per_gram, carbs_per_gram FROM nutrient_data WHERE food_name LIKE ? LIMIT 1";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + foodName.trim() + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    NutrientInfo info = new NutrientInfo();
                    info.setCaloriesPerGram(rs.getDouble("calories_per_gram"));
                    info.setProteinPerGram(rs.getDouble("protein_per_gram"));
                    info.setFatPerGram(rs.getDouble("fat_per_gram"));
                    info.setCarbsPerGram(rs.getDouble("carbs_per_gram"));
                    return info;
                } else {
                    throw new SQLException("Nutritional data not found for food: " + foodName);
                }
            }
        }
    }
}
