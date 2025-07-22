/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.dao;

import com.nutrisci.model.Profile;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProfileDAO {
    Profile save(Profile profile) throws SQLException;           // For creating new profiles
    Profile insert(Profile profile) throws SQLException;        // Alternative method name
    void update(Profile profile) throws SQLException;           // For updating existing profiles
    Optional<Profile> findById(int id) throws SQLException;     // Use Optional<Profile> consistently
    List<Profile> findAll() throws SQLException;
    void delete(int id) throws SQLException;
}
