package com.nutrisci.logic;

import com.nutrisci.dao.InMemoryNutritionDAO;
import com.nutrisci.model.Meal;
import com.nutrisci.service.AnalysisModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SwapEngineTest {
    private SwapEngine engine;

    @BeforeEach
    void setUp() {
        // Use the in-memory DAO for predictable calorie values.
        InMemoryNutritionDAO nutritionDao = new InMemoryNutritionDAO();
        // Initialize AnalysisModule with the in-memory NutritionDAO.
        AnalysisModule analysis = new AnalysisModule(nutritionDao);
        // Initialize SwapEngine with the AnalysisModule and NutritionDAO.
        engine = new SwapEngine(analysis, nutritionDao);
    }

    @Test
    void testSuggestSwap_knownGoalAndFood() {
        String swap = engine.suggestSwap("Bread", "reduce calories");
        assertEquals("Lettuce wrap", swap);

        swap = engine.suggestSwap("White Rice", "reduce calories");
        assertEquals("Cauliflower rice", swap);

        swap = engine.suggestSwap("White Bread", "increase fiber");
        assertEquals("Whole wheat bread", swap);
    }

    @Test
    void testSuggestSwap_unknownGoalOrFood() {
        // Unknown goal
        String swap = engine.suggestSwap("Bread", "gain protein");
        assertEquals("No suggestion available", swap);

        // Known goal but unknown food
        swap = engine.suggestSwap("Pasta", "reduce calories");
        assertEquals("No suggestion available", swap);
    }

    @Test
    void testApplySwap() {
        Meal original = new Meal("Snack", LocalDateTime.now());
        original.setProfileId(1);
        original.getIngredients().put("Apple", 100.0);
        original.getIngredients().put("Bread", 50.0);

        // Swap out "Bread" for "Banana"
        Meal swapped = engine.applySwap(original, "Bread", "Banana", 75.0);

        // Original unchanged
        assertTrue(original.getIngredients().containsKey("Bread"));
        assertFalse(swapped.getIngredients().containsKey("Bread"));

        // New ingredient present
        assertEquals(75.0, swapped.getIngredients().get("Banana"));
        // Other ingredient retained
        assertEquals(100.0, swapped.getIngredients().get("Apple"));
    }

    @Test
    void testCompareCalories() throws Exception {
        // Original meal: 100g Apple → 0.5 kcal/g → 50 kcal
        Meal orig = new Meal("Snack", LocalDateTime.now());
        orig.getIngredients().put("Apple", 100.0);

        // Swapped meal: 100g Banana → 0.89 kcal/g → 89 kcal
        Meal swap = new Meal("Snack", LocalDateTime.now());
        swap.getIngredients().put("Banana", 100.0);

        double diff = engine.compareCalories(orig, swap);
        // 89 - 50 = 39 kcal increase
        assertEquals(39.0, diff, 1e-6);
    }
}
