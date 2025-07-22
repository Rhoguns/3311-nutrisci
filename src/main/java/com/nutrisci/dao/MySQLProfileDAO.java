/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.dao;

import com.nutrisci.model.Profile;
import com.nutrisci.connector.DatabaseConnector; // Correct import
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySQLProfileDAO implements ProfileDAO {

    @Override
    public Profile save(Profile profile) throws SQLException {
        if (profile.getId() == 0) {
            return insert(profile);
        } else {
            update(profile);
            return profile;
        }
    }

    @Override
    public Profile insert(Profile profile) throws SQLException {
        String sql = "INSERT INTO profiles (name, sex, date_of_birth, height_cm, weight_kg, unit, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, profile.getName());
            ps.setString(2, profile.getSex());
            ps.setDate(3, Date.valueOf(profile.getDateOfBirth()));
            ps.setDouble(4, profile.getHeightCm());
            ps.setDouble(5, profile.getWeightKg());
            ps.setString(6, profile.getUnit());
            ps.setString(7, profile.getEmail());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating profile failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    profile.setId(generatedKeys.getInt(1));
                    System.out.println("Profile saved to database with ID: " + profile.getId());
                    return profile;
                } else {
                    throw new SQLException("Creating profile failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public void update(Profile profile) throws SQLException {
        String sql = "UPDATE profiles SET name = ?, sex = ?, date_of_birth = ?, height_cm = ?, weight_kg = ?, unit = ?, email = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, profile.getName());
            ps.setString(2, profile.getSex());
            ps.setDate(3, Date.valueOf(profile.getDateOfBirth()));
            ps.setDouble(4, profile.getHeightCm());
            ps.setDouble(5, profile.getWeightKg());
            ps.setString(6, profile.getUnit());
            ps.setString(7, profile.getEmail());
            ps.setInt(8, profile.getId());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating profile failed, profile not found.");
            }
            System.out.println("Profile updated in database: ID " + profile.getId());
        }
    }

    @Override
    public Optional<Profile> findById(int id) throws SQLException {
        String sql = "SELECT * FROM profiles WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Profile profile = new Profile();
                    profile.setId(rs.getInt("id"));
                    profile.setName(rs.getString("name"));
                    profile.setSex(rs.getString("sex"));
                    profile.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
                    profile.setHeightCm(rs.getDouble("height_cm"));
                    profile.setWeightKg(rs.getDouble("weight_kg"));
                    profile.setUnit(rs.getString("unit"));
                    profile.setEmail(rs.getString("email"));
                    return Optional.of(profile);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Profile> findAll() throws SQLException {
        List<Profile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM profiles";
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Profile profile = new Profile();
                profile.setId(rs.getInt("id"));
                profile.setName(rs.getString("name"));
                profile.setSex(rs.getString("sex"));
                profile.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
                profile.setHeightCm(rs.getDouble("height_cm"));
                profile.setWeightKg(rs.getDouble("weight_kg"));
                profile.setUnit(rs.getString("unit"));
                profile.setEmail(rs.getString("email"));
                profiles.add(profile);
            }
        }
        return profiles;
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM profiles WHERE id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting profile failed, profile not found.");
            }
        }
    }
}
