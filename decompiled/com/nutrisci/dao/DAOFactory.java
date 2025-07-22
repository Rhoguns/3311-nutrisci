/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.dao;

import com.nutrisci.dao.ExerciseDAO;
import com.nutrisci.dao.InMemoryExerciseDAO;
import com.nutrisci.dao.MealDAO;
import com.nutrisci.dao.MySQLMealDAO;
import com.nutrisci.dao.MySQLNutritionDAO;
import com.nutrisci.dao.MySQLProfileDAO;
import com.nutrisci.dao.NutritionDAO;
import com.nutrisci.dao.ProfileDAO;
import com.nutrisci.dao.SwapRuleDAO;

public class DAOFactory {
    public static MealDAO getMealDAO() {
        return new MySQLMealDAO();
    }

    public static NutritionDAO getNutritionDAO() {
        return new MySQLNutritionDAO();
    }

    public static ProfileDAO getProfileDAO() {
        return new MySQLProfileDAO();
    }

    public static SwapRuleDAO getSwapRuleDAO() {
        throw new Error("Unresolved compilation problems: \n\tSyntax error on token \"AppliedSwapDAO\", record expected\n\tAppliedSwapDAO cannot be resolved to a type\n\tIllegal modifier for parameter getAppliedSwapDAO; only final is permitted\n\tSyntax error, insert \"ClassBody\" to complete RecordDeclaration\n");
    }

    public static ExerciseDAO getExerciseDAO() {
        return new InMemoryExerciseDAO();
    }

    private DAOFactory() {
    }
}
