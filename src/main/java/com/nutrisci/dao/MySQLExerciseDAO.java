package com.nutrisci.dao;

import com.nutrisci.model.Exercise;
import java.sql.SQLException;
import java.util.List;

public class MySQLExerciseDAO
implements ExerciseDAO {
    @Override
    public int insert(Exercise exercise) throws SQLException {
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public Exercise findById(int id) throws SQLException {
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public List<Exercise> findAll() throws SQLException {
        throw new Error("Unresolved compilation problems: \n\tUnhandled exception type Throwable\n\tUnhandled exception type Throwable\n\tUnhandled exception type Throwable\n");
    }
}
