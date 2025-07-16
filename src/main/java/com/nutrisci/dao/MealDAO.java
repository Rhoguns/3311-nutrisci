package com.nutrisci.dao;

import com.nutrisci.model.Meal;
import java.sql.SQLException;
import java.util.List;

public interface MealDAO {
    /**
     * Inserts a new meal record into the database.
     *
     * @param m The Meal object to be inserted.
     * @throws SQLException If a database access error occurs.
     */
    void insert(Meal m) throws SQLException;

    /**
     * Retrieves a list of meals associated with a specific user profile.
     *
     * @param profileId The ID of the profile for which to retrieve meals.
     * @return A List of Meal objects belonging to the specified profile.
     * @throws SQLException If a database access error occurs.
     */
    List<Meal> findByProfile(int profileId) throws SQLException;
}