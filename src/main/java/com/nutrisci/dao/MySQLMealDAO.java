package com.nutrisci.dao;

import com.nutrisci.connector.DatabaseConnector;
import com.nutrisci.model.Meal;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MySQLMealDAO implements MealDAO {

    @Override
    public void insert(Meal m) throws SQLException {
        String mealSql =
            "INSERT INTO meals (profile_id, meal_type, logged_at) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(mealSql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, m.getProfileId());
            ps.setString(2, m.getType());
            ps.setTimestamp(3, Timestamp.valueOf(m.getLoggedAt()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    m.setId(rs.getInt(1));
                }
            }
        }

        // SQL query to insert meal ingredients.
        // It specifies the columns meal_id, food_name, and quantity_g.
        String ingSql =
            "INSERT INTO meal_ingredients (meal_id, food_name, quantity_g) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps2 = conn.prepareStatement(ingSql)) {

            // Iterate over each ingredient entry (food name and quantity) in the meal.
            // Set the meal ID, food name, and quantity for each ingredient.
            for (var entry : m.getIngredients().entrySet()) {
                ps2.setInt(1, m.getId());
                ps2.setString(2, entry.getKey());    // food_name
                ps2.setDouble(3, entry.getValue());  // quantity_g
                ps2.addBatch();
            }
            ps2.executeBatch();
        }
    }

    @Override
    public List<Meal> findByProfile(int profileId) throws SQLException {
        // SQL query to select meal details for a specific profile.
        String mealSql =
            "SELECT id, meal_type, logged_at FROM meals WHERE profile_id = ?";
        // Initialize a list to store the retrieved Meal objects.
        List<Meal> meals = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(mealSql)) {

            // Set the profile ID parameter for the query.
            ps.setInt(1, profileId);
            try (ResultSet rs = ps.executeQuery()) {
                // Iterate through the result set to populate Meal objects.
                while (rs.next()) {
                    // Create a new Meal object for each row.
                    Meal m = new Meal();
                    m.setId(rs.getInt("id"));
                    // Set the profile ID, type, and logged-at timestamp for the meal.
                    m.setProfileId(profileId);
                    m.setType(rs.getString("meal_type"));
                    m.setLoggedAt(rs.getTimestamp("logged_at").toLocalDateTime());
                    meals.add(m);
                }
            }

            // SQL query to select ingredients for a specific meal.
            String ingSql =
                "SELECT food_name, quantity_g FROM meal_ingredients WHERE meal_id = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(ingSql)) {
                // Iterate through each meal to load its ingredients.
                for (Meal m : meals) {
                    // Set the meal ID parameter for the ingredient query.
                    ps2.setInt(1, m.getId());
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        // Iterate through the ingredient result set and add to the meal's ingredients map.
                        while (rs2.next()) {
                            m.getIngredients()
                             .put(rs2.getString("food_name"),
                                  rs2.getDouble("quantity_g"));
                        }
                    }
                }
            }
        }

        return meals;
    }
}
