package com.nutrisci.dao;

import com.nutrisci.connector.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MySQLNutritionDAO implements NutritionDAO {

    /**
     * Retrieves the calories per gram for a given food item from the database.
     * The search is case-insensitive and uses a LIKE operator for partial matches.
     *
     * @param foodName The name of the food item to search for.
     * @return The calories per gram if found, otherwise 0.0.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public double getCaloriesPerGram(String foodName) throws SQLException {
        // SQL query to select calories_per_gram from the nutrient_data table.
        // It uses a case-insensitive LIKE search for food_name and limits the result to 1.
        String sql = """
            SELECT calories_per_gram
              FROM nutrient_data
             WHERE LOWER(food_name) LIKE LOWER(CONCAT('%', ?, '%'))
             LIMIT 1
        """;
        // Establish a connection and prepare the statement.
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, foodName.trim()); // Set the foodName parameter, trimming whitespace.
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { // If a result is found, return the calories_per_gram.
                    return rs.getDouble("calories_per_gram"); // Retrieve and return the value.
                }
            }
        }
        return 0.0;
    }

    @Override
    public Map<String, Double> getNutrientBreakdown(String foodName) throws SQLException {
        /**
         * Retrieves the nutrient breakdown (protein, carbs, fat, fibre) per gram for a given food item from the database.
         * The search is case-insensitive and uses a LIKE operator for partial matches.
         *
         * @param foodName The name of the food item to search for.
         * @return A Map containing the nutrient breakdown if found, otherwise an empty map.
         * @throws SQLException If a database access error occurs.
         */
        String sql = """
            SELECT
              protein_per_gram,
              carbs_per_gram,
              fat_per_gram,
              fibre_per_gram
            FROM nutrient_data
            WHERE LOWER(food_name) LIKE LOWER(CONCAT('%', ?, '%'))
            LIMIT 1
        """;
        // Establish a connection and prepare the statement.
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, foodName.trim()); // Set the foodName parameter, trimming whitespace.
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { // If a result is found, populate and return the breakdown map.
                    Map<String, Double> breakdown = new HashMap<>();
                    // Retrieve and put each nutrient value into the map.
                    breakdown.put("protein", rs.getDouble("protein_per_gram"));
                    breakdown.put("carbs",   rs.getDouble("carbs_per_gram"));
                    breakdown.put("fat",     rs.getDouble("fat_per_gram"));
                    breakdown.put("fibre",   rs.getDouble("fibre_per_gram"));
                    return breakdown;
                }
            }
        }
        return Map.of();
    }
}
