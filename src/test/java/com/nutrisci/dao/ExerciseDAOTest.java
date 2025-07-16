package com.nutrisci.dao;

import com.nutrisci.model.Exercise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseDAOTest {
    private ExerciseDAO dao;

    @BeforeEach
    void setUp() {
        // use the in-memory implementation for fast, isolated testing
        // Initializes the DAO with an in-memory implementation for testing.
        dao = new InMemoryExerciseDAO();
    }

    @Test
    void testInsertAndFindById() throws SQLException {
        // Creates a new Exercise object.
        Exercise ex = new Exercise();
        // Sets the profile ID for the exercise.
        ex.setProfileId(42);
        // Sets the name of the exercise.
        ex.setName("Push-ups");
        // Sets the duration of the exercise in minutes.
        ex.setDurationMinutes(15);
        // Sets the calories burned during the exercise.
        ex.setCaloriesBurned(50);
        // Sets the timestamp when the exercise was performed.
        ex.setPerformedAt(LocalDateTime.of(2025, 7, 14, 18, 0));

        // Inserts the exercise into the DAO and retrieves the generated ID.
        int id = dao.insert(ex);
        // Asserts that the generated ID is positive.
        assertTrue(id > 0, "Generated ID should be positive");

        // Fetches the exercise by its ID.
        Exercise fetched = dao.findById(id);
        // Asserts that the fetched exercise is not null.
        assertNotNull(fetched, "Should find the exercise by ID");
        // Asserts that the ID, profile ID, name, duration, calories burned, and performed at timestamp match the original exercise.
        assertEquals(id, fetched.getId());
        assertEquals(42, fetched.getProfileId());
        assertEquals("Push-ups", fetched.getName());
        assertEquals(15, fetched.getDurationMinutes());
        assertEquals(50, fetched.getCaloriesBurned());
        assertEquals(LocalDateTime.of(2025, 7, 14, 18, 0), fetched.getPerformedAt());
    }

    @Test
    void testFindAll() throws SQLException {
        // insert multiple exercises
        // Creates the first Exercise object.
        Exercise ex1 = new Exercise();
        // Sets properties for the first exercise.
        ex1.setProfileId(1);
        ex1.setName("Jogging");
        ex1.setDurationMinutes(30);
        ex1.setCaloriesBurned(200);
        ex1.setPerformedAt(LocalDateTime.now());
        // Inserts the first exercise and stores its ID.
        int id1 = dao.insert(ex1);

        // Creates the second Exercise object.
        Exercise ex2 = new Exercise();
        // Sets properties for the second exercise.
        ex2.setProfileId(2);
        ex2.setName("Cycling");
        ex2.setDurationMinutes(45);
        ex2.setCaloriesBurned(400);
        ex2.setPerformedAt(LocalDateTime.now());
        // Inserts the second exercise and stores its ID.
        int id2 = dao.insert(ex2);

        // Retrieves all exercises from the DAO.
        List<Exercise> all = dao.findAll();
        // Asserts that the total number of exercises returned is 2.
        assertEquals(2, all.size(), "Should return both inserted exercises");
        // Asserts that the list contains the first inserted exercise by its ID.
        assertTrue(all.stream().anyMatch(e -> e.getId() == id1));
        // Asserts that the list contains the second inserted exercise by its ID.
        assertTrue(all.stream().anyMatch(e -> e.getId() == id2));
    }
}