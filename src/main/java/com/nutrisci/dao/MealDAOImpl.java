package com.nutrisci.dao;

import com.nutrisci.connector.DatabaseConnector;
import com.nutrisci.model.Meal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealDAOImpl implements MealDAO {

    /**
     * Inserts a new meal and its ingredients within a single transaction.
     */
    @Override
    public void insert(Meal meal) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection()) {
            conn.setAutoCommit(false);
            insert(meal, conn);
            conn.commit();
        }
    }

    /**
     * Inserts a meal and its ingredients using the database.

     */
    @Override
    public void insert(Meal meal, Connection conn) throws SQLException {

        String insertMealSql = "INSERT INTO meals (profile_id, meal_type, logged_at) VALUES (?, ?, ?)";
        
        try (PreparedStatement psMeal = conn.prepareStatement(insertMealSql, Statement.RETURN_GENERATED_KEYS)) {
            psMeal.setInt(1, meal.getProfileId());
            psMeal.setString(2, meal.getType());
            
            if (meal.getDate() != null) {
                LocalDate localDate = meal.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                psMeal.setTimestamp(3, Timestamp.valueOf(localDate.atStartOfDay()));
            } else {
                psMeal.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            }
            
            psMeal.executeUpdate();


            try (ResultSet generatedKeys = psMeal.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    meal.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating meal failed, no ID obtained.");
                }
            }
        }

        String insertLogsSql = "INSERT INTO meal_logs (meal_id, food_name, quantity) VALUES (?, ?, ?)";
        
        try (PreparedStatement psLogs = conn.prepareStatement(insertLogsSql)) {
            for (Map.Entry<String, Double> ingredient : meal.getIngredients().entrySet()) {
                psLogs.setInt(1, meal.getId());
                psLogs.setString(2, ingredient.getKey());
                psLogs.setDouble(3, ingredient.getValue());
                psLogs.addBatch();
            }
            psLogs.executeBatch();
        }
    }

    @Override
    public List<Meal> findByProfileId(int profileId) throws SQLException {
        String sql = "SELECT m.id as meal_id, m.meal_type, m.logged_at, ml.food_name, ml.quantity " +
                     "FROM meals m " +
                     "JOIN meal_logs ml ON m.id = ml.meal_id " +
                     "WHERE m.profile_id = ? " +
                     "ORDER BY m.logged_at DESC, m.id";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, profileId);
            return executeQueryAndMapResults(ps, profileId);
        }
    }

    @Override
    public List<Meal> findAll() throws SQLException {
        String sql = "SELECT m.id as meal_id, m.profile_id, m.meal_type, m.logged_at, ml.food_name, ml.quantity " +
                     "FROM meals m " +
                     "JOIN meal_logs ml ON m.id = ml.meal_id " +
                     "ORDER BY m.logged_at DESC, m.id";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            return executeQueryAndMapResults(ps, -1); // -1 indicates no specific profile
        }
    }

    @Override
    public List<Meal> findByProfileAndDateRange(int profileId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT m.id as meal_id, m.meal_type, m.logged_at, ml.food_name, ml.quantity " +
                     "FROM meals m " +
                     "JOIN meal_logs ml ON m.id = ml.meal_id " +
                     "WHERE m.profile_id = ? AND DATE(m.logged_at) BETWEEN ? AND ? " +
                     "ORDER BY m.logged_at DESC, m.id";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, profileId);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));
            return executeQueryAndMapResults(ps, profileId);
        }
    }

    @Override
    public List<Meal> findByProfileIdAndDateRange(int profileId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT m.id as meal_id, m.meal_type, m.logged_at, ml.food_name, ml.quantity " +
                     "FROM meals m " +
                     "JOIN meal_logs ml ON m.id = ml.meal_id " +
                     "WHERE m.profile_id = ? AND DATE(m.logged_at) BETWEEN ? AND ? " +
                     "ORDER BY m.logged_at DESC, m.id";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, profileId);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));
            return executeQueryAndMapResults(ps, profileId);
        }
    }

    @Override
    public void delete(int mealId, Connection conn) throws SQLException {
        String deleteMealLogsSql = "DELETE FROM meal_logs WHERE meal_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteMealLogsSql)) {
            ps.setInt(1, mealId);
            ps.executeUpdate();
        }
        
        String deleteMealSql = "DELETE FROM meals WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteMealSql)) {
            ps.setInt(1, mealId);
            ps.executeUpdate();
        }
    }

    private List<Meal> executeQueryAndMapResults(PreparedStatement ps, int defaultProfileId) throws SQLException {
        ResultSet rs = ps.executeQuery();
        Map<Integer, Meal> mealMap = new HashMap<>();
        
        while (rs.next()) {
            int mealId = rs.getInt("meal_id");
            
            Meal meal = mealMap.get(mealId);
            if (meal == null) {
                String mealType = rs.getString("meal_type");
                Timestamp loggedAtTimestamp = rs.getTimestamp("logged_at");
                int profileId = (defaultProfileId != -1) ? defaultProfileId : rs.getInt("profile_id");
                
                LocalDateTime loggedAt = loggedAtTimestamp.toLocalDateTime();
                meal = new Meal(mealType, loggedAt);
                
                meal.setId(mealId);
                meal.setProfileId(profileId);
                mealMap.put(mealId, meal);
            }
            
            String foodName = rs.getString("food_name");
            double quantity = rs.getDouble("quantity");
            meal.addIngredient(foodName, quantity);
        }
        
        return new ArrayList<>(mealMap.values());
    }
}