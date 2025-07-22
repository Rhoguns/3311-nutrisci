package com.nutrisci.ui;

import com.nutrisci.dao.DAOFactory;
import com.nutrisci.dao.ExerciseDAO;
import com.nutrisci.model.Exercise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseLoggerUITest {
    
    @Mock private ExerciseDAO mockExerciseDAO;
    
    private ExerciseLoggerUI exerciseLoggerUI;
    private JTextField tfProfileId, tfName, tfDuration, tfCalories, tfPerformedAt;
    private JButton btnSave;

    @BeforeEach
    void setUp() throws Exception {
        // Fix: Pass the mock DAO to the constructor
        exerciseLoggerUI = new ExerciseLoggerUI(mockExerciseDAO);
        
        // Get the actual field names from the ExerciseLoggerUI class
        tfProfileId = getField(exerciseLoggerUI, "tfProfileId");
        tfName = getField(exerciseLoggerUI, "tfName");
        tfDuration = getField(exerciseLoggerUI, "tfDuration");
        btnSave = getField(exerciseLoggerUI, "logButton");
    }

    @Test
    void testTC07_ExerciseLogging_Running() throws Exception {
        // TC07: Exercise logging
        when(mockExerciseDAO.insert(any(Exercise.class))).thenReturn(1);
        
        // Initial Condition: TestUser profile active
        tfProfileId.setText("1");
        tfName.setText("Morning Run");
        tfDuration.setText("30");
        tfCalories.setText("150");
        tfPerformedAt.setText("2025-07-19 10:00"); // Valid datetime format
        
        // Procedure: Save exercise
        btnSave.doClick();
        
        // Expected Outcome: Exercise stored
        verify(mockExerciseDAO).insert(argThat(exercise -> {
            return exercise.getProfileId() == 1 &&
                   exercise.getName().equals("Morning Run") &&
                   exercise.getDurationMinutes() == 30.0 &&
                   exercise.getCaloriesBurned() == 150.0;
        }));
    }

    @Test
    void testTC07_ExerciseLogging_MissingProfileId() throws Exception {
        // Test validation: missing profile ID
        tfProfileId.setText(""); // Empty profile ID
        tfName.setText("Morning Run");
        tfDuration.setText("30");
        tfCalories.setText("150");
        tfPerformedAt.setText("2025-07-19 10:00");
        
        // Procedure: Try to save
        btnSave.doClick();
        
        // Expected Outcome: No exercise should be saved due to validation error
        verify(mockExerciseDAO, never()).insert(any(Exercise.class));
    }

    @Test
    void testTC07_ExerciseLogging_InvalidDuration() throws Exception {
        // Test validation: invalid duration
        tfProfileId.setText("1");
        tfName.setText("Morning Run");
        tfDuration.setText("invalid"); // Invalid number
        tfCalories.setText("150");
        tfPerformedAt.setText("2025-07-19 10:00");
        
        // Procedure: Try to save
        btnSave.doClick();
        
        // Expected Outcome: No exercise should be saved due to validation error
        verify(mockExerciseDAO, never()).insert(any(Exercise.class));
    }

    @Test
    void testExerciseLoggerUIConstruction() {
        // Assuming a default constructor or a constructor that can be called with null/mocks
        ExerciseLoggerUI exerciseLoggerUI = new ExerciseLoggerUI(null); // Pass null or a mock controller if needed
        assertNotNull(exerciseLoggerUI);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(Object obj, String fieldName) throws Exception {
        var field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(obj);
    }
}