package com.nutrisci.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NutritionDAOTest {
    private NutritionDAO dao;

    @BeforeEach
    void setUp() {
        // use the in-memory implementation for fast, isolated testing
        dao = new InMemoryNutritionDAO();
    }

    @Test
    void testGetCaloriesPerGram_existingFoods() throws SQLException {
        // Assert that the calories per gram for "Apple" are correctly retrieved.
        assertEquals(0.5,  dao.getCaloriesPerGram("Apple"),  1e-6);
        // Assert that the calories per gram for "Banana" are correctly retrieved.
        assertEquals(0.89, dao.getCaloriesPerGram("Banana"), 1e-6);
    }

    @Test
    void testGetCaloriesPerGram_nonexistentFood() throws SQLException {
        assertEquals(0.0, dao.getCaloriesPerGram("Dragonfruit"), 1e-6);
    }

    @Test
    void testGetNutrientBreakdown_existingFoods() throws SQLException {
        Map<String, Double> apple = dao.getNutrientBreakdown("Apple");
        assertEquals(4, apple.size(), "Apple should have 4 nutrients");
        assertEquals(0.003, apple.get("protein"), 1e-6);
        assertEquals(0.14,  apple.get("carbs"),   1e-6);
        assertEquals(0.001, apple.get("fat"),     1e-6);
        assertEquals(0.02,  apple.get("fibre"),   1e-6);

        Map<String, Double> banana = dao.getNutrientBreakdown("Banana");
        assertEquals(4, banana.size(), "Banana should have 4 nutrients");
        assertEquals(0.011, banana.get("protein"), 1e-6);
        assertEquals(0.23,  banana.get("carbs"),   1e-6);
        assertEquals(0.003, banana.get("fat"),     1e-6);
        assertEquals(0.025, banana.get("fibre"),   1e-6);
    }

    @Test
    void testGetNutrientBreakdown_nonexistentFood() throws SQLException {
        Map<String, Double> empty = dao.getNutrientBreakdown("Papaya");
        assertTrue(empty.isEmpty(), "Nonexistent food should return an empty map");
    }
}