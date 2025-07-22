package com.nutrisci.dao;

import com.nutrisci.connector.DatabaseConnector;
import com.nutrisci.model.Meal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLMealDAO implements MealDAO {

    @Override
    public void insert(Meal meal) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection()) {
            insert(meal, conn);
        }
    }

    @Override
    public void insert(Meal meal, Connection conn) throws SQLException {
        String mealSql = "INSERT INTO meal (profile_id, meal_type, logged_at) VALUES (?, ?, ?)";
        String ingredientSql = "INSERT INTO meal_ingredient (meal_id, food_name, quantity_grams) VALUES (?, ?, ?)";

        boolean originalAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false); // Start transaction

        try (PreparedStatement psMeal = conn.prepareStatement(mealSql, Statement.RETURN_GENERATED_KEYS)) {
            psMeal.setInt(1, meal.getProfileId());
            psMeal.setString(2, meal.getMealType());
            psMeal.setTimestamp(3, Timestamp.valueOf(meal.getLoggedAt()));
            psMeal.executeUpdate();

            int mealId;
            try (ResultSet generatedKeys = psMeal.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    mealId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating meal failed, no ID obtained.");
                }
            }

            try (PreparedStatement psIngredient = conn.prepareStatement(ingredientSql)) {
                for (Map.Entry<String, Double> ingredient : meal.getIngredients().entrySet()) {
                    psIngredient.setInt(1, mealId);
                    psIngredient.setString(2, ingredient.getKey());
                    psIngredient.setDouble(3, ingredient.getValue());
                    psIngredient.addBatch();
                }
                psIngredient.executeBatch();
            }
            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            conn.rollback(); // Rollback on error
            throw e;
        } finally {
            conn.setAutoCommit(originalAutoCommit); // Restore original auto-commit
        }
    }

    @Override
    public List<Meal> findByProfileId(int profileId) throws SQLException {
        List<Meal> meals = new ArrayList<>();
        String sql = "SELECT m.id, m.profile_id, m.meal_type, m.logged_at, " +
                     "mi.food_name, mi.quantity_grams " +
                     "FROM meal m " +
                     "LEFT JOIN meal_ingredient mi ON m.id = mi.meal_id " +
                     "WHERE m.profile_id = ? " +
                     "ORDER BY m.id";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, profileId);

            try (ResultSet rs = ps.executeQuery()) {
                Map<Integer, Meal> mealMap = new HashMap<>();
                
                while (rs.next()) {
                    int mealId = rs.getInt("id");
                    
                    if (!mealMap.containsKey(mealId)) {
                        Meal meal = new Meal();
                        meal.setId(mealId);
                        meal.setProfileId(rs.getInt("profile_id"));
                        meal.setMealType(rs.getString("meal_type"));
                        meal.setLoggedAt(rs.getTimestamp("logged_at").toLocalDateTime());
                        meal.setIngredients(new HashMap<>());
                        mealMap.put(mealId, meal);
                    }
                    
                    // Add ingredient if it exists
                    String foodName = rs.getString("food_name");
                    if (foodName != null) {
                        double quantity = rs.getDouble("quantity_grams");
                        mealMap.get(mealId).getIngredients().put(foodName, quantity);
                    }
                }
                
                meals.addAll(mealMap.values());
            }
        }
        return meals;
    }

    @Override
    public List<Meal> findByProfileAndDateRange(int profileId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return findByProfileIdAndDateRange(profileId, startDate, endDate);
    }

    @Override
    public List<Meal> findByProfileIdAndDateRange(int profileId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Meal> meals = new ArrayList<>();
        String sql = "SELECT m.id, m.profile_id, m.meal_type, m.logged_at, " +
                     "mi.food_name, mi.quantity_grams " +
                     "FROM meal m " +
                     "LEFT JOIN meal_ingredient mi ON m.id = mi.meal_id " +
                     "WHERE m.profile_id = ? AND m.logged_at >= ? AND m.logged_at < ? " +
                     "ORDER BY m.id";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, profileId);
            ps.setTimestamp(2, Timestamp.valueOf(startDate.atStartOfDay()));
            ps.setTimestamp(3, Timestamp.valueOf(endDate.plusDays(1).atStartOfDay()));

            try (ResultSet rs = ps.executeQuery()) {
                Map<Integer, Meal> mealMap = new HashMap<>();
                
                while (rs.next()) {
                    int mealId = rs.getInt("id");
                    
                    if (!mealMap.containsKey(mealId)) {
                        Meal meal = new Meal();
                        meal.setId(mealId);
                        meal.setProfileId(rs.getInt("profile_id"));
                        meal.setMealType(rs.getString("meal_type"));
                        meal.setLoggedAt(rs.getTimestamp("logged_at").toLocalDateTime());
                        meal.setIngredients(new HashMap<>());
                        mealMap.put(mealId, meal);
                    }
                    
                    // Add ingredient if it exists
                    String foodName = rs.getString("food_name");
                    if (foodName != null) {
                        double quantity = rs.getDouble("quantity_grams");
                        mealMap.get(mealId).getIngredients().put(foodName, quantity);
                    }
                }
                
                meals.addAll(mealMap.values());
            }
        }
        return meals;
    }

    @Override
    public List<Meal> findAll() throws SQLException {
        List<Meal> meals = new ArrayList<>();
        String sql = "SELECT m.id, m.profile_id, m.meal_type, m.logged_at, " +
                     "mi.food_name, mi.quantity_grams " +
                     "FROM meal m " +
                     "LEFT JOIN meal_ingredient mi ON m.id = mi.meal_id " +
                     "ORDER BY m.id";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = ps.executeQuery()) {
                Map<Integer, Meal> mealMap = new HashMap<>();
                
                while (rs.next()) {
                    int mealId = rs.getInt("id");
                    
                    if (!mealMap.containsKey(mealId)) {
                        Meal meal = new Meal();
                        meal.setId(mealId);
                        meal.setProfileId(rs.getInt("profile_id"));
                        meal.setMealType(rs.getString("meal_type"));
                        meal.setLoggedAt(rs.getTimestamp("logged_at").toLocalDateTime());
                        meal.setIngredients(new HashMap<>());
                        mealMap.put(mealId, meal);
                    }
                    
                    // Add ingredient if it exists
                    String foodName = rs.getString("food_name");
                    if (foodName != null) {
                        double quantity = rs.getDouble("quantity_grams");
                        mealMap.get(mealId).getIngredients().put(foodName, quantity);
                    }
                }
                
                meals.addAll(mealMap.values());
            }
        }
        return meals;
    }

    @Override
    public void delete(int mealId, Connection conn) throws SQLException {
        String sql = "DELETE FROM meal WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mealId);
            ps.executeUpdate();
        }
    }
}
