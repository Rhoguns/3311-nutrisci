package com.nutrisci.dao;

import com.nutrisci.connector.DatabaseConnector;
import com.nutrisci.model.AppliedSwap;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MySQLAppliedSwapDAO implements AppliedSwapDAO {

    @Override
    public AppliedSwap insert(AppliedSwap appliedSwap) throws SQLException {
        String sql = "INSERT INTO applied_swaps (profile_id, swap_rule_id, original_qty, new_qty, date, applied_at, meal_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
        	setAppliedSwapParameters(ps, appliedSwap);
        	
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    appliedSwap.setId(generatedKeys.getInt(1));
                }
            }
        }
        return appliedSwap;
    }

    @Override
    public AppliedSwap findById(int id) throws SQLException {
        String sql = "SELECT * FROM applied_swaps WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAppliedSwap(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<AppliedSwap> findByProfile(int profileId) throws SQLException {
        List<AppliedSwap> appliedSwaps = new ArrayList<>();
        String sql = "SELECT * FROM applied_swaps WHERE profile_id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, profileId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    appliedSwaps.add(mapResultSetToAppliedSwap(rs));
                }
            }
        }
        return appliedSwaps;
    }

    @Override
    public List<AppliedSwap> findByProfileAndDateRange(int profileId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<AppliedSwap> appliedSwaps = new ArrayList<>();
        String sql = "SELECT * FROM applied_swaps WHERE profile_id = ? AND date >= ? AND date <= ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, profileId);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    appliedSwaps.add(mapResultSetToAppliedSwap(rs));
                }
            }
        }
        return appliedSwaps;
    }

    @Override
    public AppliedSwap update(AppliedSwap appliedSwap) throws SQLException {
        String sql = "UPDATE applied_swaps SET profile_id = ?, swap_rule_id = ?, original_qty = ?, new_qty = ?, date = ?, applied_at = ?, meal_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
        	setAppliedSwapParameters(ps, appliedSwap);
            ps.setInt(8, appliedSwap.getId());
            
            ps.executeUpdate();
        }
        return appliedSwap;
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM applied_swaps WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private AppliedSwap mapResultSetToAppliedSwap(ResultSet rs) throws SQLException {
        AppliedSwap appliedSwap = new AppliedSwap();
        appliedSwap.setId(rs.getInt("id"));
        appliedSwap.setProfileId(rs.getInt("profile_id"));
        appliedSwap.setSwapRuleId(rs.getInt("swap_rule_id"));
        appliedSwap.setOriginalQty(rs.getDouble("original_qty"));
        appliedSwap.setNewQty(rs.getDouble("new_qty"));
        appliedSwap.setDate(rs.getDate("date").toLocalDate());
        appliedSwap.setAppliedAt(rs.getTimestamp("applied_at").toLocalDateTime());
        appliedSwap.setMealId(rs.getInt("meal_id"));
        return appliedSwap;
    }
    
    //Helper setter method for shared AppliedSwap parameters for insert and update methods
    private void setAppliedSwapParameters(PreparedStatement ps, AppliedSwap appliedSwap) throws SQLException{
    	int index = 1;
    	
    	ps.setInt(index++, appliedSwap.getProfileId() );
    	ps.setInt(index++, appliedSwap.getSwapRuleId() );
    	ps.setDouble(index++, appliedSwap.getOriginalQty() );
    	ps.setDouble(index++, appliedSwap.getNewQty() );
    	ps.setDate(index++, Date.valueOf(appliedSwap.getDate()) );
    	ps.setTimestamp(index++, Timestamp.valueOf(appliedSwap.getAppliedAt()) );
    	ps.setInt(index++, appliedSwap.getMealId() );
    }
}
