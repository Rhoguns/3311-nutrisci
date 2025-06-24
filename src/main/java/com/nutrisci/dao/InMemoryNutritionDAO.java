package main.java.com.nutrisci.dao;

import java.util.HashMap;
import java.util.Map;


// This class provides an in-memory implementation of the NutritionDAO interface.
// It uses a static map to store calorie values for some common foods.

public class InMemoryNutritionDAO implements NutritionDAO {
    private static final Map<String, Double> CAL_CACHE = new HashMap<>();
    static {
        CAL_CACHE.put("egg", 1.55);
        CAL_CACHE.put("bread", 2.5);
        CAL_CACHE.put("tomato", 0.18);
    }

    @Override
    public double getCaloriesPerGram(String foodName) {
        return CAL_CACHE.getOrDefault(foodName.toLowerCase(), 1.0);
    }

    @Override
    public Map<String, Double> getNutrientBreakdownPerGram(String foodName) {
        // stub: return calories only
        Map<String, Double> map = new HashMap<>();
        map.put("calories", getCaloriesPerGram(foodName));
        return map;
    }
}
