/*
 * Decompiled with CFR 0.152.
 */
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
        String sql = "SELECT * FROM swap_rules";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                SwapRule rule = new SwapRule();
                rule.setId(rs.getInt("id"));
                rule.setOriginalFood(rs.getString("original_food"));
                // FIX: Use the correct column name 'suggested_food'
                rule.setSuggestedFood(rs.getString("suggested_food")); 
                rule.setGoal(rs.getString("goal"));
                rules.add(rule);
            }
        }
        return rules;
    }

    @Override
    public SwapRule findById(int id) throws SQLException {
        String sql = "SELECT * FROM swap_rules WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SwapRule rule = new SwapRule();
                    rule.setId(rs.getInt("id"));
                    rule.setOriginalFood(rs.getString("original_food"));
                    // FIX: Use the correct column name 'suggested_food'
                    rule.setSuggestedFood(rs.getString("suggested_food"));
                    rule.setGoal(rs.getString("goal"));
                    return rule;
                }
            }
        }
        return null;
    }

    @Override
    public List<SwapRule> findByGoal(String goal) throws SQLException {
        List<SwapRule> rules = new ArrayList<>();
        String sql = "SELECT * FROM swap_rules WHERE goal = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, goal);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SwapRule rule = new SwapRule();
                    rule.setId(rs.getInt("id"));
                    rule.setOriginalFood(rs.getString("original_food"));
                    // FIX: Use the correct column name 'suggested_food'
                    rule.setSuggestedFood(rs.getString("suggested_food"));
                    rule.setGoal(rs.getString("goal"));
                    rules.add(rule);
                }
            }
        }
        return rules;
    }

    @Override
    public void insert(SwapRule swapRule) throws SQLException {
        String sql = "INSERT INTO swap_rules (original_food, replacement_food, goal) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, swapRule.getOriginalFood());
            // FIX: Use the correct column name 'suggested_food'
            ps.setString(2, swapRule.getSuggestedFood());
            ps.setString(3, swapRule.getGoal());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating swap rule failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    swapRule.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating swap rule failed, no ID obtained.");
                }
            }
        }
    }
}
