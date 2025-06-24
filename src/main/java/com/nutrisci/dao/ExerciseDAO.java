package main.java.com.nutrisci.dao;

/**
 * Data Access Object for exercise burn rates.
 */
public interface ExerciseDAO {
    /**
     * Returns burn rate in kcal/minute for the given exercise type.
     */
    double getBurnRatePerMinute(String exerciseType);
}

