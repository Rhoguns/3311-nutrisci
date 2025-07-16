package com.nutrisci.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.nutrisci.connector.DatabaseConnector;
import com.nutrisci.dao.ProfileDAO;
import com.nutrisci.model.Profile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

class ProfileControllerTest {
    // Controller instance for testing profile operations.
    private static ProfileController controller;

    @BeforeAll
    // Setup method executed once before all tests.
    static void setup() throws Exception {
        // Load schema from resource file.
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            InputStream is = ProfileControllerTest.class.getResourceAsStream("/schema.sql");
            assertNotNull(is, "schema.sql not found");
            String sql = new BufferedReader(new InputStreamReader(is))
                               .lines().collect(Collectors.joining("\n"));
            for (String stmtSql : sql.split(";")) {
                if (!stmtSql.trim().isEmpty()) {
                    stmt.execute(stmtSql);
                }
            }
        }
        // Initialize the controller with a new ProfileDAO.
        controller = new ProfileController(new ProfileDAO());
    }

    @Test
    // Test case for creating a new profile and then fetching it by its ID.
    void testCreateAndFetchProfile() throws Exception {
        Profile p = new Profile();
        p.setName("Alice");
        p.setSex("F");
        p.setDateOfBirth(LocalDate.of(1990, 5, 20));
        p.setHeightCm(165);
        p.setWeightKg(60);
        p.setUnit("metric");

        // Create the profile and assert that an ID is generated.
        Profile created = controller.createProfile(p);
        assertTrue(created.getId() > 0);

        Optional<Profile> opt = controller.getProfileById(created.getId());
        // Assert that the profile was found and its details match the created profile.
        assertTrue(opt.isPresent());
        Profile loaded = opt.get();
        assertEquals("Alice", loaded.getName());
        assertEquals("F",      loaded.getSex());
        assertEquals(LocalDate.of(1990,5,20), loaded.getDateOfBirth());
        assertEquals(165,      loaded.getHeightCm());
        assertEquals(60,       loaded.getWeightKg());
        assertEquals("metric", loaded.getUnit());
    }
}