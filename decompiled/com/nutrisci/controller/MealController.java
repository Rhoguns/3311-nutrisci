/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.controller;

import com.nutrisci.dao.MealDAO;
import com.nutrisci.model.Meal;
import java.sql.SQLException;
import java.util.List;

public class MealController {
    private final MealDAO dao;

    public MealController(MealDAO dao) {
        this.dao = dao;
    }

    public Meal logMeal(Meal m) throws SQLException {
        this.dao.insert(m);
        return m;
    }

    public List<Meal> getMealsForProfile(int profileId) throws SQLException {
        return this.dao.findByProfile(profileId);
    }
}
