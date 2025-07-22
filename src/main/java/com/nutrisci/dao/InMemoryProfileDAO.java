package com.nutrisci.dao;

import com.nutrisci.model.Profile;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryProfileDAO implements ProfileDAO {
    private final Map<Integer, Profile> profiles = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);
    
    @Override
    public Profile insert(Profile profile) throws SQLException {
        // Generate ID for new profile
        int newId = idCounter.getAndIncrement();
        profile.setId(newId);
        profiles.put(newId, profile);
        System.out.println("Profile saved with ID: " + newId);
        return profile;
    }
    
    @Override
    public Profile save(Profile profile) throws SQLException {
        if (profile.getId() == 0) {
            // New profile - generate ID and save
            return insert(profile);
        } else {
            // Existing profile - update
            update(profile);
            return profile;
        }
    }
    
    @Override
    public Optional<Profile> findById(int id) throws SQLException {
        return Optional.ofNullable(profiles.get(id));
    }
    
    @Override
    public void update(Profile profile) throws SQLException {
        if (profile.getId() == 0) {
            throw new SQLException("Profile ID is required for update");
        }
        profiles.put(profile.getId(), profile);
        System.out.println("Profile updated: ID " + profile.getId());
    }
    
    @Override
    public void delete(int id) throws SQLException {
        profiles.remove(id);
    }
    
    @Override
    public List<Profile> findAll() throws SQLException {
        return new ArrayList<>(profiles.values());
    }
}