package com.nutrisci.controller;

import com.nutrisci.model.Profile;
import com.nutrisci.dao.ProfileDAO;
import java.sql.SQLException;
import java.util.Optional;

public class ProfileController {
    // Data Access Object for interacting with profile data.
    private final ProfileDAO dao;

    /**
     * Constructs a new ProfileController with the given ProfileDAO.
     *
     * @param dao The Data Access Object for Profile entities.
     */
    public ProfileController(ProfileDAO dao) {
        this.dao = dao;
    }

    /**
     * Creates a new user profile by inserting it into the database.
     *
     * @param p The Profile object to be created.
     * @return The Profile object after it has been inserted (potentially with an updated ID).
     * @throws SQLException If a database access error occurs.
     */
    public Profile createProfile(Profile p) throws SQLException {
        dao.insert(p);
        return p;
    }

    /**
     * Retrieves a user profile by its ID.
     *
     * @param id The ID of the profile to retrieve.
     * @return An Optional containing the Profile object if found, or an empty Optional if not found.
     * @throws SQLException If a database access error occurs.
     */
    public Optional<Profile> getProfileById(int id) throws SQLException {
        return Optional.ofNullable(dao.findById(id));
    }
}