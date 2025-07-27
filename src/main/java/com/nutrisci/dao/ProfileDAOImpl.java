/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.dao;

import com.nutrisci.connector.DatabaseConnector;
import com.nutrisci.model.Profile;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfileDAOImpl
implements ProfileDAO {
    private static final String SELECT_COLUMNS = "id, name, sex, date_of_birth, height_cm, weight_kg, unit, email";

    @Override
    public Profile save(Profile profile) throws SQLException {
        if (profile.getId() > 0) {
            this.update(profile);
            return profile;
        }
        return this.insert(profile);
    }

    @Override
    public Profile insert(Profile profile) throws SQLException {
        String sql = "INSERT INTO profiles (name, sex, date_of_birth, height_cm, weight_kg, unit, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, profile.getName());
            ps.setString(2, profile.getSex());
            ps.setDate(3, Date.valueOf(profile.getDateOfBirth()));
            ps.setDouble(4, profile.getHeightCm());
            ps.setDouble(5, profile.getWeightKg());
            ps.setString(6, profile.getUnit());
            ps.setString(7, profile.getEmail());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    profile.setId(rs.getInt(1));
                }
            }
        }
        return profile;
    }

    @Override
    public Optional<Profile> findById(int id) throws SQLException {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM profiles WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(this.mapResultSetToProfile(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Profile> findAll() throws SQLException {
        List<Profile> profiles = new ArrayList<Profile>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM profiles";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                profiles.add(this.mapResultSetToProfile(rs));
            }
        }
        return profiles;
    }

    @Override
    public void update(Profile profile) throws SQLException {
        String sql = "UPDATE profiles SET name = ?, sex = ?, date_of_birth = ?, height_cm = ?, weight_kg = ?, unit = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, profile.getName());
            ps.setString(2, profile.getSex());
            ps.setDate(3, Date.valueOf(profile.getDateOfBirth()));
            ps.setDouble(4, profile.getHeightCm());
            ps.setDouble(5, profile.getWeightKg());
            ps.setString(6, profile.getUnit());
            ps.setString(7, profile.getEmail());
            ps.setInt(8, profile.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM profiles WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Profile mapResultSetToProfile(ResultSet rs) throws SQLException {
        Profile profile = new Profile();
        profile.setId(rs.getInt("id"));
        profile.setName(rs.getString("name"));
        profile.setSex(rs.getString("sex"));
        profile.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        profile.setHeightCm(rs.getDouble("height_cm"));
        profile.setWeightKg(rs.getDouble("weight_kg"));
        profile.setUnit(rs.getString("unit"));
        profile.setEmail(rs.getString("email"));
        return profile;
    }
}
