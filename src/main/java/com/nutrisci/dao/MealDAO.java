package com.nutrisci.dao;

import com.nutrisci.model.Meal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface MealDAO {
    /**
     * Saves meal to database.
     */
    void insert(Meal meal) throws SQLException;

    /**
     * Saves meal using existing connection.
     */
    void insert(Meal meal, Connection conn) throws SQLException;

    /**
     * Gets meals for user.
     */
    List<Meal> findByProfileId(int profileId) throws SQLException;

    /**
     * Gets meals in date range.
     */
    List<Meal> findByProfileAndDateRange(int profileId, LocalDate startDate, LocalDate endDate) throws SQLException;

    /**
     * Gets meals for profile in date range.
     */
    List<Meal> findByProfileIdAndDateRange(int profileId, LocalDate startDate, LocalDate endDate) throws SQLException;

    /**
     * Finds all meals in the database.
     */
    List<Meal> findAll() throws SQLException;

    /**
     * Deletes a meal by its ID using a provided database connection.
     */
    void delete(int mealId, Connection conn) throws SQLException;
}
