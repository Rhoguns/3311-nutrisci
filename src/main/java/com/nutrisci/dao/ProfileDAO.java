package com.nutrisci.dao;

import com.nutrisci.model.Profile;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Profile database operations.
 */
public interface ProfileDAO {
    /**
     * Saves profile.
     */
    Profile save(Profile profile) throws SQLException;
    
    /**
     * Creates new profile.
     */
    Profile insert(Profile profile) throws SQLException;
    
    /**
     * Updates profile.
     */
    void update(Profile profile) throws SQLException;
    
    /**
     * Finds a profile by its unique identifier.
     * 
     * @param id the profile ID to search for
     * @return an Optional containing the profile if found, empty Optional otherwise
     * @throws SQLException if a database access error occurs
     */
    Optional<Profile> findById(int id) throws SQLException;
    
    /**
     * Retrieves all profiles from the database.
     * 
     * @return a list of all profiles
     * @throws SQLException if a database access error occurs
     */
    List<Profile> findAll() throws SQLException;
    
    /**
     * Deletes a profile from the database.
     * 
     * @param id the ID of the profile to delete
     * @throws SQLException if a database access error occurs
     */
    void delete(int id) throws SQLException;
}
