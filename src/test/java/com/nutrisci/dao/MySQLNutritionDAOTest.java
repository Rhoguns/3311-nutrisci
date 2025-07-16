package com.nutrisci.dao;

import static org.junit.jupiter.api.Assertions.*;

import com.nutrisci.connector.DatabaseConnector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Map;
import java.util.stream.Collectors;

class MySQLNutritionDAOTest {
    // Data Access Object for MySQL nutrition operations.
    private static MySQLNutritionDAO dao;

    @BeforeAll
    // Setup method executed once before all tests.
    static void setup() throws Exception {
        // Establish a connection to the database and create a statement.
        // Load schema
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            InputStream is = MySQLNutritionDAOTest.class.getResourceAsStream("/schema.sql");
            assertNotNull(is, "schema.sql not found on classpath");
            String sql = new BufferedReader(new InputStreamReader(is))
                               // Read all lines from the InputStream and join them into a single string.
                               .lines().collect(Collectors.joining("\n"));
            // Split the SQL string into individual statements and execute each one.
            for (String stmtSql : sql.split(";")) {
                if (!stmtSql.trim().isEmpty()) {
                    stmt.execute(stmtSql);
                }
            }
        }
        // Initialize the DAO with a new MySQLNutritionDAO.
        dao = new MySQLNutritionDAO();
    }

    // Test case for retrieving calories per gram for a food item.
    @Test
    void testGetCaloriesPerGram() throws SQLException {
        // insert test row
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO nutrient_data(food_name, calories_per_gram) VALUES(?,?)")
        ) {
            ps.setString(1, "Apple");
            ps.setDouble(2, 0.52);
            ps.executeUpdate();
        }
        // Assert that the calories per gram for "Apple" are correctly retrieved.
        assertEquals(0.52, dao.getCaloriesPerGram("Apple"));
        // Assert that a non-existent food returns 0.0 calories per gram.
        assertEquals(0.0,  dao.getCaloriesPerGram("Nonexistent"));
    }

    // Test case for retrieving the nutrient breakdown of a food item.
    @Test
    void testGetNutrientBreakdown() throws SQLException {
        // insert breakdown row
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO nutrient_data(food_name, protein_per_gram, carbs_per_gram, fat_per_gram, fibre_per_gram) "
               + "VALUES(?,?,?,?,?)")
        ) {
            ps.setString(1, "Oatmeal");
            ps.setDouble(2, 0.017);
            ps.setDouble(3, 0.66);
            ps.setDouble(4, 0.07);
            ps.setDouble(5, 0.10);
            ps.executeUpdate();
        }
        // Retrieve the nutrient breakdown for "Oatmeal".
        Map<String, Double> map = dao.getNutrientBreakdown("Oatmeal");
        // Assert that each nutrient value in the map is correct.
        assertEquals(0.017, map.get("protein"));
        assertEquals(0.66,  map.get("carbs"));
        assertEquals(0.07,  map.get("fat"));
        assertEquals(0.10,  map.get("fibre"));
        // Assert that a non-existent food returns an empty map.
        assertTrue(dao.getNutrientBreakdown("Nothing").isEmpty());
    }
}