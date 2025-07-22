/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.dao;

import com.nutrisci.model.Meal;
import java.sql.SQLException;
import java.util.List;

public interface MealDAO {
    public void insert(Meal var1) throws SQLException;

    public List<Meal> findByProfile(int var1) throws SQLException;
}
