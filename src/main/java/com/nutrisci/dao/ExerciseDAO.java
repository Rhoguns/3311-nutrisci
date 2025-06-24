package main.java.com.nutrisci.dao;

/**
 * Data Access Object interface for retrieving exercise burn rates.
 */
public interface ExerciseDAO {
    /**
     * Returns the calorie burn rate for a given exercise type.
     *
     * @param exerciseType the name of the exercise (e.g., "Running")
     * @return the burn rate in kcal per minute
     */
    double getBurnRatePerMinute(String exerciseType);
}
