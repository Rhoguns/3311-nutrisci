package com.nutrisci.ui;

import com.nutrisci.controller.ProfileController; // Import the controller
import com.nutrisci.dao.ProfileDAO;
import com.nutrisci.model.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileUITest {
    
    @Mock
    private ProfileDAO mockProfileDAO;
    
    private ProfileController profileController; // Add a controller instance
    private ProfileUI profileUI;
    private JTextField tfName, tfDob, tfHeight, tfWeight;
    private JComboBox<String> cbSex, cbUnit;
    private JButton btnSave;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Create a real controller with the mock DAO
        profileController = new ProfileController(mockProfileDAO);
        
        // 2. Create the UI with the controller
        profileUI = new ProfileUI(profileController);
        
        // 3. Get UI components for testing
        tfName = getField(profileUI, "tfName");
        cbSex = getField(profileUI, "cbSex");
        tfDob = getField(profileUI, "tfDob");
        tfHeight = getField(profileUI, "tfHeight");
        tfWeight = getField(profileUI, "tfWeight");
        cbUnit = getField(profileUI, "cbUnit");
        btnSave = getField(profileUI, "btnSave");
    }

    @Test
    void testTC01_ProfileCreation_ValidData() throws Exception {
        // TC01: Valid profile creation
        // Procedure: Enter valid data
        tfName.setText("TestUser");
        cbSex.setSelectedItem("M");
        tfDob.setText("1998-01-15");
        tfHeight.setText("180");
        tfWeight.setText("75");
        cbUnit.setSelectedItem("metric");
        
        // Click Save
        btnSave.doClick();
        
        // Expected Outcome: The controller's createProfile method is called, which in turn calls the DAO's insert method.
        verify(mockProfileDAO).insert(any(Profile.class));
        
        // Verify fields are set correctly
        assertEquals("TestUser", tfName.getText());
    }

    @Test 
    void testTC02_ProfileCreation_MissingName() throws Exception {
        // TC02: Missing required field validation
        // Initial Condition: Application running, no profile exists
        // Procedure: Leave name blank, fill other fields
        tfName.setText(""); // Blank name
        cbSex.setSelectedItem("F");
        tfDob.setText("1993-05-20");
        tfHeight.setText("165");
        tfWeight.setText("60");
        cbUnit.setSelectedItem("metric");
        
        // Click Save
        btnSave.doClick();
        
        // Expected Outcome: Error message, profile not saved
        verify(mockProfileDAO, never()).insert(any(Profile.class));
    }

    @Test
    void testTC03_ProfileEditing_UpdateWeight() throws Exception {
        // TC03: Profile editing
        Profile existingProfile = new Profile();
        existingProfile.setId(1);
        existingProfile.setName("TestUser");
        existingProfile.setSex("M");
        existingProfile.setDateOfBirth(LocalDate.of(1993, 6, 15));
        existingProfile.setWeightKg(65.0);
        existingProfile.setHeightCm(170.0);
        existingProfile.setUnit("metric");
        
        // REMOVE THIS LINE: The 'update' operation doesn't require a 'findById' call first.
        // when(mockProfileDAO.findById(1)).thenReturn(existingProfile);
        
        // Simulate loading the profile into the UI
        profileUI.setCurrentProfile(existingProfile);
        
        // Procedure: Change weight
        tfWeight.setText("60");
        
        // Click Save
        btnSave.doClick();
        
        // Expected Outcome: The controller's updateProfile method is called, which calls the DAO's update method.
        verify(mockProfileDAO).update(any(Profile.class));
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(Object obj, String fieldName) throws Exception {
        var field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(obj);
    }
}