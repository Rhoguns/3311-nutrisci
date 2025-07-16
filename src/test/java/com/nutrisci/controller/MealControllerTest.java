package com.nutrisci.controller;

import com.nutrisci.connector.DatabaseConnector;
import com.nutrisci.dao.MySQLMealDAO;
import com.nutrisci.dao.MealDAO;
import com.nutrisci.model.Meal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MealControllerTest {
    // Controller instance for testing meal operations.
    private static MealController controller;
    // Stores the ID of the profile used for testing.
    private static int profileId;

    @BeforeAll
    // Setup method executed once before all tests.
    static void setup() throws Exception {
        // Load schema
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {

            InputStream is = MealControllerTest.class.getResourceAsStream("/schema.sql");
            assertNotNull(is, "schema.sql not found on classpath");
            String sql = new BufferedReader(new InputStreamReader(is))
                               .lines()
                               .collect(Collectors.joining("\n"));

            for (String stmtSql : sql.split(";")) {
                if (!stmtSql.trim().isEmpty()) {
                    stmt.execute(stmtSql.trim());
                }
            }

            // Insert a profile to satisfy the foreign key constraint
            try (PreparedStatement ps = conn.prepareStatement(
                   "INSERT INTO profiles(name, sex, date_of_birth, height_cm, weight_kg, unit) VALUES (?, ?, ?, ?, ?, ?)",
                   Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, "TestUser");
                ps.setString(2, "M");
                ps.setDate(3, Date.valueOf("1990-01-01"));
                ps.setDouble(4, 175.0);
                ps.setDouble(5, 70.0);
                ps.setString(6, "metric");
                ps.executeUpdate();

                // Retrieve the generated profile ID.
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    assertTrue(rs.next());
                    profileId = rs.getInt(1);
                }
            }
        }

        // Initialize controller with real DAO
        controller = new MealController(new MySQLMealDAO());
    }

    @Test
    // Test case for logging and retrieving a meal.
    void testLogAndRetrieveMeal() throws SQLException {
        // Create a new Meal
        Meal m = new Meal();
        m.setProfileId(profileId);
        m.setType("Dinner");
        m.setLoggedAt(LocalDateTime.of(2025, 7, 14, 19, 0));
        
        // Prepare ingredients for the meal.
        Map<String, Double> ingredients = new HashMap<>();
        ingredients.put("Steak", 200.0);
        ingredients.put("Vegetables", 150.0);
        m.setIngredients(ingredients);

        // Log the meal
        Meal logged = controller.logMeal(m);
        assertTrue(logged.getId() > 0, "Logged meal should have a generated ID");

        // Retrieve meals for this profile
        List<Meal> meals = controller.getMealsForProfile(profileId);
        assertEquals(1, meals.size());
        Meal fetched = meals.get(0);

        assertEquals("Dinner", fetched.getType());
        assertEquals(2, fetched.getIngredients().size());
        assertEquals(200.0, fetched.getIngredients().get("Steak"));
        assertEquals(150.0, fetched.getIngredients().get("Vegetables"));
    }
}