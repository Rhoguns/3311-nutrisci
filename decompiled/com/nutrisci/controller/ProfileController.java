/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.controller;

import com.nutrisci.dao.ProfileDAO;
import com.nutrisci.model.Profile;
import java.sql.SQLException;
import java.util.Optional;

public class ProfileController {
    private final ProfileDAO dao;

    public ProfileController(ProfileDAO dao) {
        this.dao = dao;
    }

    public Profile createProfile(Profile p) throws SQLException {
        this.dao.insert(p);
        return p;
    }

    public Optional<Profile> getProfileById(int id) throws SQLException {
        return Optional.ofNullable(this.dao.findById(id));
    }
}
