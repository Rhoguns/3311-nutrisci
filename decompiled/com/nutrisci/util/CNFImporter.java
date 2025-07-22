/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.util;

import java.io.IOException;
import java.sql.SQLException;

public class CNFImporter {
    public static void main(String[] args) {
        try {
            CNFImporter.importFoodNames();
            CNFImporter.importNutrientAmounts();
            CNFImporter.importFoodGroups();
            CNFImporter.importNutrientNames();
            CNFImporter.testImport();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void importFoodNames() throws SQLException, IOException {
        throw new Error("Unresolved compilation problems: \n\tUnhandled exception type Throwable\n\tUnhandled exception type Throwable\n\tUnhandled exception type Throwable\n");
    }

    private static void importNutrientAmounts() throws SQLException, IOException {
        throw new Error("Unresolved compilation problems: \n\tUnhandled exception type Throwable\n\tUnhandled exception type Throwable\n\tUnhandled exception type Throwable\n");
    }

    private static void importFoodGroups() throws SQLException, IOException {
        throw new Error("Unresolved compilation problems: \n\tUnhandled exception type Throwable\n\tUnhandled exception type Throwable\n\tUnhandled exception type Throwable\n");
    }

    private static void importNutrientNames() throws SQLException, IOException {
        throw new Error("Unresolved compilation problems: \n\tUnhandled exception type Throwable\n\tUnhandled exception type Throwable\n\tUnhandled exception type Throwable\n");
    }

    private static String[] parseCSVLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    }

    private static void testImport() throws SQLException {
        throw new Error("Unresolved compilation problem: \n\tUnhandled exception type Throwable\n");
    }
}
