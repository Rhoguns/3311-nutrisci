package com.nutrisci.dao;

import com.nutrisci.model.Exercise;
import com.nutrisci.connector.DatabaseConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MySQLExerciseDAO implements ExerciseDAO {

    @Override
    public int insert(Exercise exercise) throws SQLException {
        String sql = "INSERT INTO exercises "
                   + "(profile_id, name, duration_minutes, calories_burned, performed_at) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, exercise.getProfileId());
            ps.setString(2, exercise.getName());
            ps.setDouble(3, exercise.getDurationMinutes());
            ps.setDouble(4, exercise.getCaloriesBurned());
            ps.setTimestamp(5, Timestamp.valueOf(exercise.getPerformedAt()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    exercise.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    @Override
    public Exercise findById(int id) throws SQLException {
        String sql = "SELECT * FROM exercises WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Exercise ex = new Exercise();
                    ex.setId(rs.getInt("id"));
                    ex.setProfileId(rs.getInt("profile_id"));
                    ex.setName(rs.getString("name"));
                    ex.setDurationMinutes(rs.getDouble("duration_minutes"));
                    ex.setCaloriesBurned(rs.getDouble("calories_burned"));
                    ex.setPerformedAt(rs.getTimestamp("performed_at").toLocalDateTime());
                    return ex;
                }
            }
        }
        return null;
    }

    @Override
    public List<Exercise> findAll() throws SQLException {
        // SQL query to select all exercises from the 'exercises' table.
        String sql = "SELECT * FROM exercises";
        // Initialize an empty list to store the retrieved Exercise objects.
        List<Exercise> list = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             // Execute the query and get the result set.
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Exercise ex = new Exercise();
                ex.setId(rs.getInt("id"));
                ex.setProfileId(rs.getInt("profile_id"));
                ex.setName(rs.getString("name"));
                ex.setDurationMinutes(rs.getDouble("duration_minutes"));
                ex.setCaloriesBurned(rs.getDouble("calories_burned"));
                ex.setPerformedAt(rs.getTimestamp("performed_at").toLocalDateTime());
                // Add the populated Exercise object to the list.
                list.add(ex);
            }
        }
        return list;
    }
}
