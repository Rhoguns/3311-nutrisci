/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.dao;

import com.nutrisci.model.Meal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface MealDAO {
    /**
     * Inserts a new meal record, including its ingredients, into the database.
     */
    void insert(Meal meal) throws SQLException;

    /**
     * Inserts a new meal record using a provided connection.
     */
    void insert(Meal meal, Connection conn) throws SQLException;

    /**
     * Finds all meals for a given profile.
     */
    List<Meal> findByProfileId(int profileId) throws SQLException;

    /**
     * Finds all meals for a given profile within a specific date range.
     */
    List<Meal> findByProfileAndDateRange(int profileId, LocalDate startDate, LocalDate endDate) throws SQLException;

    /**
     * Finds all meals for a given profile within a specific date range.
     * This method name is used by AnalysisModule.
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
