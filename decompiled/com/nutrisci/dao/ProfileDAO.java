/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.dao;

import com.nutrisci.model.Profile;
import java.sql.SQLException;
import java.util.List;

public interface ProfileDAO {
    public Profile insert(Profile var1) throws SQLException;

    public Profile update(Profile var1) throws SQLException;

    public Profile findById(int var1) throws SQLException;

    public List<Profile> findAll() throws SQLException;

    public boolean delete(int var1) throws SQLException;
}
