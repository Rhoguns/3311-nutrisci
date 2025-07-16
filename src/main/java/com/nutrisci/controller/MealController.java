package com.nutrisci.controller;

import com.nutrisci.model.Meal;
import com.nutrisci.dao.MealDAO;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller class for managing meal-related operations.
 * This class acts as an intermediary between the application's UI/service layer
 * and the data access layer (MealDAO) for meal objects.
 */
public class MealController {
    private final MealDAO dao;

    /**
     * Constructs a new MealController with the given MealDAO.
     *
     * @param dao The Data Access Object for Meal entities.
     */
    public MealController(MealDAO dao) {
        this.dao = dao;
    }

    /**
     * Logs a new meal by inserting it into the database.
     *
     * @param m The Meal object to be logged.
     * @return The Meal object after it has been inserted (potentially with an updated ID).
     * @throws SQLException If a database access error occurs.
     */
    public Meal logMeal(Meal m) throws SQLException {
        dao.insert(m);
        return m;
    }

    /**
     * Retrieves a list of meals associated with a specific user profile.
     *
     * @param profileId The ID of the profile for which to retrieve meals.
     * @return A List of Meal objects belonging to the specified profile.
     * @throws SQLException If a database access error occurs.
     */
    public List<Meal> getMealsForProfile(int profileId) throws SQLException {
        return dao.findByProfile(profileId);
    }
}