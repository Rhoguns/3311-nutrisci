/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.controller;

import com.nutrisci.dao.ProfileDAO;
import com.nutrisci.model.Profile;
import java.sql.SQLException;
import java.util.Optional;

public class ProfileController {
    private ProfileDAO profileDao;  
    
    public ProfileController(ProfileDAO profileDao) {
        this.profileDao = profileDao;
    }
    
    public Profile createProfile(Profile profile) throws SQLException {
        return profileDao.save(profile); 
    }
    
    public Optional<Profile> getProfileById(int id) throws Exception {
        return profileDao.findById(id);
    }
    
    public double getBMI(Profile profile) {
        double heightInMeters = profile.getHeightCm() / 100.0;
        return profile.getWeightKg() / (heightInMeters * heightInMeters);
    }
    
    public void updateProfile(Profile profile) throws Exception {
        // Add validation
        if (profile.getName() == null || profile.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        profileDao.update(profile);
    }
}
