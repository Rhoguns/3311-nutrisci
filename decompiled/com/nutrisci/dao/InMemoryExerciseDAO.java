/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.dao;

import com.nutrisci.dao.ExerciseDAO;
import com.nutrisci.model.Exercise;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryExerciseDAO
implements ExerciseDAO {
    private final Map<Integer, Exercise> store = new HashMap<Integer, Exercise>();
    private int nextId = 1;

    @Override
    public synchronized int insert(Exercise exercise) throws SQLException {
        int id = this.nextId++;
        exercise.setId(id);
        this.store.put(id, exercise);
        return id;
    }

    @Override
    public Exercise findById(int id) throws SQLException {
        return this.store.get(id);
    }

    @Override
    public List<Exercise> findAll() throws SQLException {
        return new ArrayList<Exercise>(this.store.values());
    }
}
