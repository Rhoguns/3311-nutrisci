package com.nutrisci.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.nutrisci.connector.DatabaseConnector;
import com.nutrisci.dao.MySQLNutritionDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Map;
import java.util.stream.Collectors;

class NutritionControllerTest {
    // Controller instance for testing nutrition operations.
    private static NutritionController controller;

    @BeforeAll
    // Setup method executed once before all tests.
    static void setup() throws Exception {
        // load schema
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            // Get the schema SQL file from the classpath.
            InputStream is = NutritionControllerTest.class.getResourceAsStream("/schema.sql");
            // Assert that the schema file was found.
            assertNotNull(is);
            // Read the SQL from the schema file.
            String sql = new BufferedReader(new InputStreamReader(is))
                               .lines().collect(Collectors.joining("\n"));
            // Execute each SQL statement in the schema.
            for (String stmtSql : sql.split(";")) {
                if (!stmtSql.trim().isEmpty()) stmt.execute(stmtSql);
            }
        }
        // Initialize the controller with a MySQLNutritionDAO.
        controller = new NutritionController(new MySQLNutritionDAO());
    }

    @Test
    // Test case for retrieving calories per gram for a food item.
    void testGetCaloriesPerGram() throws Exception {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO nutrient_data(food_name,calories_per_gram) VALUES(?,?)")) {
            // Insert test data for "Orange".
            ps.setString(1, "Orange");
            ps.setDouble(2, 0.47);
            ps.executeUpdate();
        }
        // Assert that the calories per gram for "Orange" are correctly retrieved.
        assertEquals(0.47, controller.getCaloriesPerGram("Orange"));
        // Assert that a non-existent food returns 0.0 calories per gram.
        assertEquals(0.0,  controller.getCaloriesPerGram("Pear"));
    }

    @Test
    // Test case for retrieving the nutrient breakdown of a food item.
    void testGetNutrientBreakdown() throws Exception {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO nutrient_data(food_name,protein_per_gram,carbs_per_gram,fat_per_gram,fibre_per_gram) "
               + "VALUES(?,?,?,?,?)")) {
            // Insert test data for "Yogurt" with its nutrient breakdown.
            ps.setString(1, "Yogurt");
            ps.setDouble(2, 0.04);
            ps.setDouble(3, 0.05);
            ps.setDouble(4, 0.03);
            ps.setDouble(5, 0.00);
            ps.executeUpdate();
        }
        // Retrieve the nutrient breakdown for "Yogurt".
        Map<String, Double> map = controller.getNutrientBreakdown("Yogurt");
        // Assert that each nutrient value in the map is correct.
        assertEquals(0.04, map.get("protein"));
        assertEquals(0.05, map.get("carbs"));
        assertEquals(0.03, map.get("fat"));
        assertEquals(0.00, map.get("fibre"));
    }
}
