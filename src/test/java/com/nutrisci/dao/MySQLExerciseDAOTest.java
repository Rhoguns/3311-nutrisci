package com.nutrisci.dao;

import static org.junit.jupiter.api.Assertions.*;

import com.nutrisci.connector.DatabaseConnector;
import com.nutrisci.model.Exercise; 
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

class MySQLExerciseDAOTest {
    // Data Access Object for Exercise operations.
    private static MySQLExerciseDAO dao;

    @BeforeAll
    // Setup method executed once before all tests.
    static void setup() throws Exception {
        // Load schema
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            // Get the schema SQL file from the classpath.
            InputStream is = MySQLExerciseDAOTest.class.getResourceAsStream("/schema.sql");
            // Assert that the schema file was found.
            assertNotNull(is, "schema.sql not found on classpath");
            // Read the SQL from the schema file.
            String sql = new BufferedReader(new InputStreamReader(is))
                               .lines().collect(Collectors.joining("\n"));
            // Execute each SQL statement in the schema.
            for (String stmtSql : sql.split(";")) {
                if (!stmtSql.trim().isEmpty()) {
                    stmt.execute(stmtSql);
                }
            }
            // insert a dummy profile for FK constraint
            // Insert a test profile to satisfy foreign key constraints for exercises.
            stmt.executeUpdate(
                "INSERT INTO profiles "
              + "(name, sex, date_of_birth, height_cm, weight_kg, unit) "
              + "VALUES ('TestUser','M','2000-01-01',180,75,'metric')",
                Statement.RETURN_GENERATED_KEYS
            );
        }
        // Initialize the DAO with a new MySQLExerciseDAO.
        dao = new MySQLExerciseDAO();
    }

    @Test
    // Test case for inserting an exercise and then finding it by its ID.
    void testInsertAndFindById() throws SQLException {
        // grab the single profile id
        int profileId;
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM profiles LIMIT 1")) {
            // Assert that a profile ID is returned.
            assertTrue(rs.next());
            // Retrieve the profile ID.
            profileId = rs.getInt(1);
        }

        // prepare an exercise
        // Create a new Exercise object.
        Exercise ex = new Exercise();
        ex.setProfileId(profileId);
        ex.setName("Jogging");
        ex.setDurationMinutes(45);
        ex.setCaloriesBurned(400);
        ex.setPerformedAt(LocalDateTime.now().withNano(0));

        // insert and verify the generated ID
        // Insert the exercise into the database and get the generated ID.
        int id = dao.insert(ex);
        // Assert that the generated ID is positive.
        assertTrue(id > 0, "insert should return generated id");

        // Fetch the exercise by its ID.
        Exercise fetched = dao.findById(id);
        // Assert that the fetched exercise is not null.
        assertNotNull(fetched);
        // Assert that the properties of the fetched exercise match the original.
        assertEquals(ex.getName(), fetched.getName());
        assertEquals(ex.getDurationMinutes(), fetched.getDurationMinutes());
        assertEquals(ex.getCaloriesBurned(), fetched.getCaloriesBurned());
        assertEquals(ex.getPerformedAt(), fetched.getPerformedAt());

        // test findAll
        // Retrieve all exercises and assert that the newly inserted exercise is present.
        List<Exercise> all = dao.findAll();
        assertTrue(all.stream().anyMatch(e -> e.getId() == id));
    }
}