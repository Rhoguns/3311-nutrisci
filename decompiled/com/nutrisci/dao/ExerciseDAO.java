/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.dao;

import com.nutrisci.model.Exercise;
import java.sql.SQLException;
import java.util.List;

public interface ExerciseDAO {
    public int insert(Exercise var1) throws SQLException;

    public Exercise findById(int var1) throws SQLException;

    public List<Exercise> findAll() throws SQLException;
}
