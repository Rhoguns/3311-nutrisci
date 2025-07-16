package com.nutrisci.dao;

import com.nutrisci.model.Exercise;
import java.sql.SQLException;
import java.util.List;

public interface ExerciseDAO {

    /**
     * Insert the given Exercise into storage.
     * @param exercise the domain object to insert
     * @return the generated ID of the new record
     */
    int insert(Exercise exercise) throws SQLException;

    /**
     * Find an Exercise by its primary key.
     * @param id the record’s ID
     * @return the Exercise, or null if not found
     */
    Exercise findById(int id) throws SQLException;

    /**
     * Return all Exercises in storage.
     * @return list of all Exercise objects (empty if none)
     */
    List<Exercise> findAll() throws SQLException;

}