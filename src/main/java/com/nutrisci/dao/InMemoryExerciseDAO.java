package com.nutrisci.dao;

import com.nutrisci.model.Exercise;

import java.sql.SQLException;
// Imports for data structures used to store exercises in memory.
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
// Imports for data structures used to store exercises in memory.
import java.util.Map;

/**
 * An in-memory implementation of the {@link ExerciseDAO} interface.
 * This class stores {@link Exercise} objects in a {@link HashMap}
 * and does not persist data to a database. It is primarily used for
 * testing or demonstration purposes where a real database is not required.
 */
public class InMemoryExerciseDAO implements ExerciseDAO {
    // A HashMap to simulate a database table, mapping exercise IDs to Exercise objects.
    private final Map<Integer, Exercise> store = new HashMap<>();
    // A simple counter to generate unique IDs for new exercises.
    private int nextId = 1;

    /**
     * Inserts a new exercise into the in-memory store.
     * A unique ID is generated and assigned to the exercise before storing.
     *
     * @param exercise the Exercise object to be inserted.
     * @return the generated ID of the inserted exercise.
     * @throws SQLException if a database access error occurs (not applicable for in-memory, but kept for interface compatibility).
     */
    @Override
    public synchronized int insert(Exercise exercise) throws SQLException {
        int id = nextId++;
        exercise.setId(id);
        store.put(id, exercise);
        return id;
    }

    /**
     * Finds an exercise by its ID in the in-memory store.
     *
     * @param id the ID of the exercise to find.
     * @return the Exercise object if found, or null if no exercise with the given ID exists.
     * @throws SQLException if a database access error occurs (not applicable for in-memory, but kept for interface compatibility).
     */
    @Override
    public Exercise findById(int id) throws SQLException {
        return store.get(id);
    }

    /**
     * Retrieves all exercises currently stored in memory.
     *
     * @return a List of all Exercise objects in the store. Returns an empty list if no exercises are stored.
     * @throws SQLException if a database access error occurs (not applicable for in-memory, but kept for interface compatibility).
     */
    @Override
    public List<Exercise> findAll() throws SQLException {
        return new ArrayList<>(store.values());
    }
}