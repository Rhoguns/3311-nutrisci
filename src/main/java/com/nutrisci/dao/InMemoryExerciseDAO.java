package main.java.com.nutrisci.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory implementation of ExerciseDAO for retrieving exercise burn rates.
 * This class uses a hardcoded cache to simulate a database of exercise types and their burn rates.
 */
public class InMemoryExerciseDAO implements ExerciseDAO {
    /** Hardcoded cache of exercise burn rates (kcal/min). */
    private static final Map<String, Double> BURN_CACHE = new HashMap<>();

    static {
        BURN_CACHE.put("running", 8.0);
        BURN_CACHE.put("walking", 4.0);
        BURN_CACHE.put("cycling", 7.0);
    }

    /**
     * Returns the calorie burn rate per minute for a given exercise type.
     * Falls back to a default rate of 5.0 kcal/min if the type is unrecognized.
     *
     * @param exerciseType the exercise type (e.g., "Running", "Walking")
     * @return burn rate in kcal per minute
     */
    @Override
    public double getBurnRatePerMinute(String exerciseType) {
        return BURN_CACHE.getOrDefault(exerciseType.toLowerCase(), 5.0);
    }
}