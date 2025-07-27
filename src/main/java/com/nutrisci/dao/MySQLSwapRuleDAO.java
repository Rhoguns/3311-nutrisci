package com.nutrisci.dao;

import com.nutrisci.connector.DatabaseConnector;
import com.nutrisci.model.SwapRule;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLSwapRuleDAO implements SwapRuleDAO {

    @Override
    public List<SwapRule> findAll() throws SQLException {
        List<SwapRule> rules = new ArrayList<>();
        String sql = "SELECT id, goal, original_food, suggested_food, improvement_value, created_at FROM swap_rules";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rules.add(mapResultSetToSwapRule(rs));
            }
        }
        return rules;
    }

    @Override
    public SwapRule findById(int id) throws SQLException {
        String sql = "SELECT id, goal, original_food, suggested_food, improvement_value, created_at FROM swap_rules WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSwapRule(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<SwapRule> findByGoal(String goal) throws SQLException {
        List<SwapRule> rules = new ArrayList<>();
        String sql = "SELECT id, goal, original_food, suggested_food, improvement_value, created_at FROM swap_rules WHERE goal = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, goal);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rules.add(mapResultSetToSwapRule(rs));
                }
            }
        }
        return rules;
    }

    @Override
    public void insert(SwapRule swapRule) throws SQLException {
        String sql = "INSERT INTO swap_rules (goal, original_food, suggested_food, improvement_value) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, swapRule.getGoal());
            ps.setString(2, swapRule.getOriginalFood());
            ps.setString(3, swapRule.getSuggestedFood());
            ps.setDouble(4, swapRule.getImprovementValue());
            
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    swapRule.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    private SwapRule mapResultSetToSwapRule(ResultSet rs) throws SQLException {
        SwapRule rule = new SwapRule();
        rule.setId(rs.getInt("id"));
        rule.setGoal(rs.getString("goal"));
        rule.setOriginalFood(rs.getString("original_food"));
        rule.setSuggestedFood(rs.getString("suggested_food"));
        rule.setImprovementValue(rs.getDouble("improvement_value"));
        return rule;
    }
}
