package com.nutrisci.service;

import static org.junit.jupiter.api.Assertions.*;

import com.nutrisci.dao.InMemoryNutritionDAO;
import com.nutrisci.model.Meal;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

class AnalysisModuleTest {

    @Test
    void testComputeTotalCaloriesWithInMemoryDAO() throws Exception {
        // InMemoryNutritionDAO has a built-in map; assume it returns 0.5 kcal/g for "Apple"
        // Initializes an in-memory NutritionDAO for testing purposes.
        InMemoryNutritionDAO dao = new InMemoryNutritionDAO();
        // Creates an AnalysisModule instance, injecting the in-memory DAO.
        AnalysisModule module = new AnalysisModule(dao);

        // Creates a new Meal object.
        Meal m = new Meal();
        // Sets the timestamp for when the meal was logged.
        m.setLoggedAt(LocalDateTime.now());
        // Sets the type of the meal (e.g., "Snack").
        m.setType("Snack");
        m.setIngredients(Map.of("Apple", 100.0));

        double total = module.computeTotalCalories(List.of(m));
        assertEquals(50.0, total, 0.0001);
    }
}
