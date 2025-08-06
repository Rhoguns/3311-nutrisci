package com.nutrisci.ui;

import com.nutrisci.controller.ProfileController;
import com.nutrisci.dao.ProfileDAOImpl;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

/**
 * Main app window.
 */
public class MainDashboard extends JFrame {
    private int profileId;
    private JTabbedPane mainTabbedPane;
    private JLabel profileLabel;
    
    // Store tab components for easier management
    private final java.util.List<TabComponent> tabComponents = new java.util.ArrayList<>();
    
    // Interface for profile-aware components
    private interface ProfileAware {
        void setProfile(int profileId);
    }
    
    // Utility class for reflection operations
    private static class ReflectionUtils {
        /**
         * Checks if an object has a specific method with given parameters.
         * @param obj The object to check
         * @param methodName The method name to look for
         * @param paramTypes The parameter types of the method
         * @return true if the method exists, false otherwise
         */
        static boolean hasMethod(Object obj, String methodName, Class<?>... paramTypes) {
            if (obj == null || methodName == null) {
                return false;
            }
            try {
                obj.getClass().getMethod(methodName, paramTypes);
                return true;
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
        
        /**
         * Safely invokes a method on an object using reflection.
         * @param obj The object to invoke the method on
         * @param methodName The method name to invoke
         * @param paramTypes The parameter types
         * @param args The arguments to pass to the method
         * @return true if invocation succeeded, false otherwise
         */
        static boolean invokeMethod(Object obj, String methodName, 
                                   Class<?>[] paramTypes, Object... args) {
            try {
                Method method = obj.getClass().getMethod(methodName, paramTypes);
                method.invoke(obj, args);
                return true;
            } catch (Exception e) {
                System.err.println("Failed to invoke " + methodName + " on " + 
                    obj.getClass().getSimpleName() + ": " + e.getMessage());
                return false;
            }
        }
    }
    
    // Wrapper class to handle profile updates uniformly
    private static class TabComponent {
        private final Component component;
        private final String title;
        private final ProfileAware profileHandler;
        
        public TabComponent(String title, Component component) {
            this.title = title;
            this.component = component;
            this.profileHandler = createProfileHandler(component);
        }
        
        /**
         * Creates a profile handler adapter for the component.
         * Uses the ProfileAware interface if available, otherwise falls back to reflection.
         */
        private ProfileAware createProfileHandler(Component comp) {
            // Check if component directly implements our interface
            if (comp instanceof ProfileAware) {
                return (ProfileAware) comp;
            } 
            
            // Check if component has setProfile method via reflection
            if (ReflectionUtils.hasMethod(comp, "setProfile", int.class)) {
                // Create adapter using reflection
                return profileId -> ReflectionUtils.invokeMethod(
                    comp, "setProfile", new Class[]{int.class}, profileId
                );
            }
            
            // Null object pattern - component doesn't support profiles
            return profileId -> {
                System.out.println(comp.getClass().getSimpleName() + 
                    " does not support profile updates");
            };
        }
        
        public void updateProfile(int profileId) {
            if (profileHandler != null) {
                profileHandler.setProfile(profileId);
            }
        }
        
        public Component getComponent() {
            return component;
        }
        
        public String getTitle() {
            return title;
        }
    }

    /**
     * Creates a new MainDashboard for the specified profile.
     * @param profileId The profile ID to load
     * @throws IllegalArgumentException if profileId is not positive
     */
    public MainDashboard(int profileId) {
        if (profileId <= 0) {
            throw new IllegalArgumentException("Profile ID must be positive");
        }
        this.profileId = profileId;
        initializeUI();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("NutriSci Dashboard - Profile " + profileId);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        
        setLayout(new BorderLayout());
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        mainTabbedPane = new JTabbedPane();
        initializeTabs();
        add(mainTabbedPane, BorderLayout.CENTER);
        
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("NutriSci Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.CENTER);
        
        // Profile management
        panel.add(createProfileManagementPanel(), BorderLayout.EAST);
        
        return panel;
    }
    
    // Extracted profile management panel creation
    private JPanel createProfileManagementPanel() {
        JPanel profilePanel = new JPanel(new FlowLayout());
        
        profileLabel = new JLabel("Profile: " + profileId);
        profileLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JButton changeProfileBtn = new JButton("Change Profile");
        changeProfileBtn.addActionListener(e -> changeProfile());
        
        profilePanel.add(profileLabel);
        profilePanel.add(changeProfileBtn);
        
        return profilePanel;
    }

    // Clean, organized tab initialization
    private void initializeTabs() {
        try {
            // Register all tabs in a clean, maintainable way
            registerTab("Profile Manager", createProfileUI());
            registerTab("Meal Logger", new MealLoggerUI());
            registerTab("Meal Journal", new MealJournalUI());
            registerTab("Compare Meals", new CompareMealsPanel());
            registerTab("Swap Analysis", new SwapImpactPanel());
            registerTab("Daily Intake Analysis", new DailyIntakePanel());
            registerTab("CFG Compliance", new CfgCompliancePanel());
            registerTab("Exercise Logger", new ExerciseLoggerUI());
            
            // Initialize all tabs with current profile
            updateAllTabs(profileId);
            
        } catch (Exception e) {
            handleTabLoadingError(e);
        }
    }
    
    // Helper method to create ProfileUI
    private Component createProfileUI() {
        ProfileController profileController = new ProfileController(new ProfileDAOImpl());
        return new ProfileUI(profileController);
    }
    
    // Clean method to register tabs
    private void registerTab(String title, Component component) {
        TabComponent tab = new TabComponent(title, component);
        tabComponents.add(tab);
        mainTabbedPane.addTab(title, component);
        component.revalidate();
    }
    
    // Simplified update method - no more message chains!
    private void updateAllTabs(int newProfileId) {
        tabComponents.forEach(tab -> tab.updateProfile(newProfileId));
    }
    
    // Extracted error handling method
    private void handleTabLoadingError(Exception e) {
        System.err.println("Error loading tabs: " + e.getMessage());
        e.printStackTrace();
        
        JPanel errorPanel = createErrorPanel(e);
        mainTabbedPane.addTab("Error", errorPanel);
    }
    
    // Create error panel for display
    private JPanel createErrorPanel(Exception e) {
        JPanel errorPanel = new JPanel(new BorderLayout());
        
        JTextArea errorText = new JTextArea(
            String.format("Error loading UI panels:\n%s\n\nPlease restart the application.",
                e.getMessage())
        );
        errorText.setEditable(false);
        errorText.setBackground(Color.LIGHT_GRAY);
        errorText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        errorPanel.add(new JScrollPane(errorText), BorderLayout.CENTER);
        return errorPanel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setBackground(new Color(245, 245, 245));
        
        // Status label
        JLabel statusLabel = new JLabel("Ready - All nutrition tracking features available", 
            SwingConstants.LEFT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        
        // Version label
        JLabel versionLabel = new JLabel("NutriSci v1.0 | EECS 3311", SwingConstants.RIGHT);
        versionLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));
        versionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        
        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(versionLabel, BorderLayout.EAST);
        
        return panel;
    }

    // Cleaner profile change logic
    private void changeProfile() {
        String input = JOptionPane.showInputDialog(this, 
            "Enter new Profile ID:", 
            "Change Profile", 
            JOptionPane.QUESTION_MESSAGE);
            
        if (input == null || input.trim().isEmpty()) {
            return; // User cancelled or entered nothing
        }
        
        try {
            int newProfileId = Integer.parseInt(input.trim());
            
            if (newProfileId <= 0) {
                showInvalidProfileError("Profile ID must be a positive number.");
                return;
            }
            
            // Update all components with new profile
            applyProfileChange(newProfileId);
            
        } catch (NumberFormatException e) {
            showInvalidProfileError("Please enter a valid numeric Profile ID.");
        }
    }
    
    // Apply profile change to all components
    private void applyProfileChange(int newProfileId) {
        updateAllTabs(newProfileId);
        updateProfileDisplay(newProfileId);
    }
    
    // Update UI elements to reflect new profile
    private void updateProfileDisplay(int newProfileId) {
        this.profileId = newProfileId;
        profileLabel.setText("Profile: " + profileId);
        setTitle("NutriSci Dashboard - Profile " + profileId);
    }
    
    // Show error dialog with custom message
    private void showInvalidProfileError(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "Invalid Input", 
            JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Main entry point for the application.
     */
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default look and feel
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            if (args.length > 0) {
                launchWithProfile(args[0]);
            } else {
                launchProfileSelector();
            }
        });
    }
    
    // Launch dashboard with specified profile
    private static void launchWithProfile(String profileArg) {
        try {
            int profileId = Integer.parseInt(profileArg);
            new MainDashboard(profileId);
        } catch (NumberFormatException e) {
            System.err.println("Invalid profile ID: " + profileArg);
            launchProfileSelector();
        }
    }
    
    // Launch profile selector when no valid profile provided
    private static void launchProfileSelector() {
        new ProfileSelector();
    }
}
