package com.nutrisci.controller;

import com.nutrisci.dao.ProfileDAO;
import com.nutrisci.model.Profile;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Controller for managing user profiles.
 */
public class ProfileController {
    /** Data access object for profile operations */
    private ProfileDAO profileDao;  
    
    /**
     * Constructs a new ProfileController with the specified ProfileDAO.
     * 
     * @param profileDao the data access object for profile operations
     */
    public ProfileController(ProfileDAO profileDao) {
        this.profileDao = profileDao;
    }
    
    /**
     * Creates a new profile in the database.
     * 
     * @param profile the profile to create
     * @return the created profile with generated ID
     * @throws SQLException if a database access error occurs
     */
    public Profile createProfile(Profile profile) throws SQLException {
        return profileDao.save(profile); 
    }
    
    /**
     * Retrieves a profile by its unique identifier.
     * 
     * @param id the profile ID to search for
     * @return an Optional containing the profile if found, empty Optional otherwise
     * @throws Exception if a database access error occurs
     */
    public Optional<Profile> getProfileById(int id) throws Exception {
        return profileDao.findById(id);
    }
    
    /**
     * Retrieves a profile by its unique identifier.
     * 
     * @param id the profile ID to search for
     * @return an Optional containing the profile if found, empty Optional otherwise
     * @throws SQLException if a database access error occurs
     */
    public Optional<Profile> getProfile(int id) throws SQLException {
        return profileDao.findById(id);
    }
    
    /**
     * Calculates the Body Mass Index (BMI) for a given profile.
     * 
     * 
     * @param profile the profile for which to calculate BMI
     * @return the calculated BMI value
     * @throws IllegalArgumentException if the profile is null
     */
    public double getBMI(Profile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }
        double heightInMeters = profile.getHeightCm() / 100.0;
        return profile.getWeightKg() / (heightInMeters * heightInMeters);
    }
    
    /**
     * Updates an existing profile in the database.
     * 
     * 
     * @param profile the profile to update 
     * @return the updated profile
     * @throws SQLException if a database access error occurs
     * @throws IllegalArgumentException if the profile name is null or empty
     */
    public Profile updateProfile(Profile profile) throws SQLException {
        if (profile.getName() == null || profile.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        profileDao.update(profile);
        return profile;
    }
}
