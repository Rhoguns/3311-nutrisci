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
        Connection conn = null;
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false);
            insert(meal, conn);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    @Override
    public void insert(Meal meal, Connection conn) throws SQLException {
        String insertMealSQL = "INSERT INTO meals (profile_id, meal_type, logged_at) VALUES (?, ?, ?)";
        String insertIngredientSQL = "INSERT INTO meal_ingredients (meal_id, food_name, quantity_g) VALUES (?, ?, ?)";

        try (PreparedStatement psMeal = conn.prepareStatement(insertMealSQL, Statement.RETURN_GENERATED_KEYS)) {
            psMeal.setInt(1, meal.getProfileId());
            psMeal.setString(2, meal.getType());
            psMeal.setTimestamp(3, Timestamp.valueOf(meal.getLoggedAt()));
            psMeal.executeUpdate();

            try (ResultSet generatedKeys = psMeal.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    meal.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating meal failed, no ID obtained.");
                }
            }
        }

        try (PreparedStatement psIngredients = conn.prepareStatement(insertIngredientSQL)) {
            for (Map.Entry<String, Double> ingredient : meal.getIngredients().entrySet()) {
                psIngredients.setInt(1, meal.getId());
                psIngredients.setString(2, ingredient.getKey());
                psIngredients.setDouble(3, ingredient.getValue());
                psIngredients.addBatch();
            }
            psIngredients.executeBatch();
        }
    }

    @Override
    public List<Meal> findByProfileId(int profileId) throws SQLException {
        String sql = "SELECT id, profile_id, meal_type, logged_at FROM meals WHERE profile_id = ?";
        List<Meal> meals = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, profileId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    meals.add(mapResultSetToMeal(rs, conn));
                }
            }
        }
        return meals;
    }

    @Override
    public List<Meal> findByProfileAndDateRange(int profileId, LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT id, profile_id, meal_type, logged_at FROM meals WHERE profile_id = ? AND DATE(logged_at) BETWEEN ? AND ?";
        List<Meal> meals = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, profileId);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    meals.add(mapResultSetToMeal(rs, conn));
                }
            }
        }
        return meals;
    }

    private Meal mapResultSetToMeal(ResultSet rs, Connection conn) throws SQLException {
        Meal meal = new Meal();
        meal.setId(rs.getInt("id"));
        meal.setProfileId(rs.getInt("profile_id"));
        meal.setType(rs.getString("meal_type"));
        meal.setLoggedAt(rs.getTimestamp("logged_at").toLocalDateTime());
        meal.setIngredients(getIngredientsForMeal(meal.getId(), conn));
        return meal;
    }

    private Map<String, Double> getIngredientsForMeal(int mealId, Connection conn) throws SQLException {
        Map<String, Double> ingredients = new HashMap<>();
        String sql = "SELECT food_name, quantity_g FROM meal_ingredients WHERE meal_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mealId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ingredients.put(rs.getString("food_name"), rs.getDouble("quantity_g"));
                }
            }
        }
        return ingredients;
    }

    @Override
    public List<Meal> findAll() throws SQLException {
        String sql = "SELECT id, profile_id, meal_type, logged_at FROM meals";
        List<Meal> meals = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                meals.add(mapResultSetToMeal(rs, conn));
            }
        }
        return meals;
    }

    public void delete(int mealId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false);
            delete(mealId, conn);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    @Override
    public void delete(int mealId, Connection conn) throws SQLException {
        String deleteIngredientsSQL = "DELETE FROM meal_ingredients WHERE meal_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteIngredientsSQL)) {
            ps.setInt(1, mealId);
            ps.executeUpdate();
        }

        String deleteMealSQL = "DELETE FROM meals WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteMealSQL)) {
            ps.setInt(1, mealId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Meal> findByProfileIdAndDateRange(int profileId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return findByProfileAndDateRange(profileId, startDate, endDate);
    }
}
