package com.nutrisci.dao;

import com.nutrisci.model.Profile;
import com.nutrisci.connector.DatabaseConnector;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProfileDAO {

    /**
 * Inserts a new Profile into the database.
 * Upon successful insertion, the generated ID from the database is set back into the Profile object.
 *
 * @param p The Profile object to be inserted.
 * @throws SQLException If a database access error occurs.
     */
    public void insert(Profile p) throws SQLException {
        // SQL query to insert a new profile record.
        String sql = "INSERT INTO profiles "
                   + "(name, sex, date_of_birth, height_cm, weight_kg, unit) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        // Establish a connection and prepare the statement, requesting generated keys.
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Set parameters for the prepared statement from the Profile object.
            ps.setString(1, p.getName());
            ps.setString(2, p.getSex());
            ps.setDate(3, Date.valueOf(p.getDateOfBirth()));
            ps.setDouble(4, p.getHeightCm());
            ps.setDouble(5, p.getWeightKg());
            ps.setString(6, p.getUnit());
            // Execute the update.
            ps.executeUpdate();

            // Retrieve the generated keys (specifically the ID).
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        }
    }

    /**
 * Fetches a Profile from the database by its unique ID.
 *
 * @param id The ID of the profile to retrieve.
 * @return The Profile object if found, or null if no profile with the given ID exists.
 * @throws SQLException If a database access error occurs.
     */
    public Profile findById(int id) throws SQLException {
        // SQL query to select a profile by its ID.
        String sql = "SELECT * FROM profiles WHERE id = ?";
        // Establish a connection and prepare the statement.
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Set the ID parameter for the query.
            ps.setInt(1, id);
            // Execute the query and process the result set.
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null; // If no row is found, return null.
                return mapRowToProfile(rs); // Map the current row to a Profile object.
            }
        }
    }

    /**
 * Returns a list of all profiles stored in the database.
 *
 * @return A List of Profile objects. Returns an empty list if no profiles are found.
 * @throws SQLException If a database access error occurs.
     */
    public List<Profile> findAll() throws SQLException {
        String sql = "SELECT * FROM profiles";
        List<Profile> result = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // Iterate through the result set and add each mapped profile to the list.
            while (rs.next()) {
                result.add(mapRowToProfile(rs));
            }
        }
        return result;
    }
    /**
 * Updates an existing Profile record in the database.
 * The update is based on the ID of the Profile object.
 *
 * @param p The Profile object containing the updated data and the ID of the profile to be updated.
 * @throws SQLException If a database access error occurs.
     */
    public void update(Profile p) throws SQLException {
        // SQL query to update an existing profile record.
        String sql = "UPDATE profiles SET "
                   + "name = ?, sex = ?, date_of_birth = ?, "
                   + "height_cm = ?, weight_kg = ?, unit = ? "
                   + "WHERE id = ?";
        // Establish a connection and prepare the statement.
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Set parameters for the prepared statement from the Profile object.
            ps.setString(1, p.getName());
            ps.setString(2, p.getSex());
            ps.setDate(3, Date.valueOf(p.getDateOfBirth()));
            ps.setDouble(4, p.getHeightCm());
            ps.setDouble(5, p.getWeightKg());
            ps.setString(6, p.getUnit());
            // Set the ID for the WHERE clause.
            ps.setInt(7, p.getId());
            // Execute the update.
            ps.executeUpdate();
        }
    }
    /**
 * Deletes a Profile record from the database by its ID.
 *
 * @param id The ID of the profile to be deleted.
 * @throws SQLException If a database access error occurs.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM profiles WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    /** Helper to map a ResultSet row to a Profile object. */
    private Profile mapRowToProfile(ResultSet rs) throws SQLException {
        Profile p = new Profile();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setSex(rs.getString("sex"));
        p.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        p.setHeightCm(rs.getDouble("height_cm"));
        p.setWeightKg(rs.getDouble("weight_kg"));
        p.setUnit(rs.getString("unit"));
        return p;
    }
}