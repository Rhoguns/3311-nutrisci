package main.java.com.nutrisci.dao;

import java.util.HashMap;
import java.util.Map;

public class InMemoryExerciseDAO implements ExerciseDAO {
    private static final Map<String, Double> BURN_CACHE = new HashMap<>();
    static {
        BURN_CACHE.put("running", 8.0);
        BURN_CACHE.put("walking", 4.0);
        BURN_CACHE.put("cycling", 7.0);
    }

    @Override
    public double getBurnRatePerMinute(String exerciseType) {
        return BURN_CACHE.getOrDefault(exerciseType.toLowerCase(), 5.0);
    }
}
