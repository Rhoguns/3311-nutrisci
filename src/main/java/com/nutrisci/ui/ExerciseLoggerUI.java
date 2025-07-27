package com.nutrisci.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.nutrisci.model.Exercise;
import com.nutrisci.dao.InMemoryExerciseDAO;
import com.nutrisci.dao.ExerciseDAO;

/**
 * Exercise logging UI.
 */
public class ExerciseLoggerUI extends JFrame {
    private int profileId = 0;
    private ExerciseDAO exerciseDAO;
    
    // UI Components
    private JLabel profileLabel;
    private JTextField exerciseNameField;
    private JComboBox<String> exerciseTypeCombo;
    private JSpinner durationSpinner;
    private JLabel caloriesLabel;
    private JTextArea logArea;
    private JButton logButton;
    private JButton clearButton;
    private JLabel statusLabel;

    public ExerciseLoggerUI() {
        exerciseDAO = new InMemoryExerciseDAO(); // Use in-memory DAO for testing
        initializeUI();
        setupEventHandlers();
    }

    private void initializeUI() {
        setTitle("NutriSci - Exercise Logger");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(600, 500);
        setLocationRelativeTo(null);

        createHeaderPanel();
        createInputPanel();
        createLogPanel();
        createStatusPanel();
    }

    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createTitledBorder("Exercise Logger"));
        headerPanel.setBackground(new Color(240, 248, 255));
        
        // Profile info
        profileLabel = new JLabel("Profile ID: " + (profileId > 0 ? profileId : "Not Set"));
        profileLabel.setFont(new Font("Arial", Font.BOLD, 14));
        profileLabel.setForeground(new Color(0, 100, 0));
        
        JLabel titleLabel = new JLabel("🏃 Exercise Logging System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        headerPanel.add(profileLabel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
    }

    private void createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Exercise Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Exercise Name
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Exercise Name:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        exerciseNameField = new JTextField(20);
        exerciseNameField.setToolTipText("Enter exercise name (e.g., Morning Run)");
        inputPanel.add(exerciseNameField, gbc);

        // Exercise Type
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Exercise Type:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        String[] exerciseTypes = {"Running", "Walking", "Cycling", "Swimming", "Weightlifting", "Yoga", "Other"};
        exerciseTypeCombo = new JComboBox<>(exerciseTypes);
        exerciseTypeCombo.setSelectedItem("Running");
        inputPanel.add(exerciseTypeCombo, gbc);

        // Duration
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Duration (minutes):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        durationSpinner = new JSpinner(new SpinnerNumberModel(30.0, 1.0, 300.0, 1.0));
        durationSpinner.setToolTipText("Enter duration in minutes");
        inputPanel.add(durationSpinner, gbc);

        // Calories Burned (calculated)
        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Estimated Calories:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        caloriesLabel = new JLabel("150.0 kcal (auto-calculated)");
        caloriesLabel.setFont(new Font("Arial", Font.BOLD, 12));
        caloriesLabel.setForeground(new Color(0, 150, 0));
        inputPanel.add(caloriesLabel, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        logButton = new JButton("🏃 Log Exercise");
        logButton.setBackground(new Color(0, 150, 0));
        logButton.setForeground(Color.WHITE);
        logButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        clearButton = new JButton("🗑️ Clear");
        clearButton.setBackground(new Color(150, 150, 150));
        clearButton.setForeground(Color.WHITE);
        
        buttonPanel.add(logButton);
        buttonPanel.add(clearButton);
        inputPanel.add(buttonPanel, gbc);

        add(inputPanel, BorderLayout.CENTER);
    }

    private void createLogPanel() {
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Exercise Log"));
        
        logArea = new JTextArea(8, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(new Color(248, 248, 248));
        logArea.setText("Exercise Log - Ready to log exercises...\n");
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(logPanel, BorderLayout.SOUTH);
    }

    private void createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        statusLabel = new JLabel("Ready - Enter exercise details and click 'Log Exercise'");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusPanel.add(statusLabel);
        
        // Add status panel to a container
        JPanel bottomContainer = (JPanel) getContentPane().getComponent(2); // Log panel
        bottomContainer.add(statusPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        // Duration spinner change listener - auto-calculate calories
        durationSpinner.addChangeListener(e -> updateCaloriesEstimate());
        
        // Exercise type change listener - auto-calculate calories
        exerciseTypeCombo.addActionListener(e -> updateCaloriesEstimate());

        // Log exercise button
        logButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logExercise();
            }
        });

        // Clear button
        clearButton.addActionListener(e -> clearFields());
    }

    private void updateCaloriesEstimate() {
        double duration = (Double) durationSpinner.getValue();
        String exerciseType = (String) exerciseTypeCombo.getSelectedItem();
        
        // Calorie burn rates per minute (simplified for testing)
        double caloriesPerMinute = switch (exerciseType) {
            case "Running" -> 5.0;
            case "Walking" -> 3.0;
            case "Cycling" -> 4.0;
            case "Swimming" -> 6.0;
            case "Weightlifting" -> 2.5;
            case "Yoga" -> 1.5;
            default -> 3.0;
        };
        
        double totalCalories = duration * caloriesPerMinute;
        caloriesLabel.setText(String.format("%.1f kcal (%.1f cal/min × %.1f min)", 
                                          totalCalories, caloriesPerMinute, duration));
    }

    private void logExercise() {
        try {
            // Validate inputs
            if (profileId <= 0) {
                showError("Profile not set. Please set a profile first.");
                return;
            }

            String exerciseName = exerciseNameField.getText().trim();
            if (exerciseName.isEmpty()) {
                showError("Exercise name is required.");
                return;
            }

            String exerciseType = (String) exerciseTypeCombo.getSelectedItem();
            double duration = (Double) durationSpinner.getValue();
            
            // Calculate calories burned
            double caloriesPerMinute = switch (exerciseType) {
                case "Running" -> 5.0;
                case "Walking" -> 3.0;
                case "Cycling" -> 4.0;
                case "Swimming" -> 6.0;
                case "Weightlifting" -> 2.5;
                case "Yoga" -> 1.5;
                default -> 3.0;
            };
            double caloriesBurned = duration * caloriesPerMinute;

            // Create exercise object
            Exercise exercise = new Exercise();
            exercise.setProfileId(profileId);
            exercise.setName(exerciseName);
            exercise.setExerciseType(exerciseType);
            exercise.setDurationMinutes(duration);
            exercise.setCaloriesBurned(caloriesBurned);
            exercise.setPerformedAt(LocalDateTime.now());

            // Save exercise
            int exerciseId = exerciseDAO.insert(exercise);
            
            // Update log area
            String logEntry = String.format("[%s] %s - %s: %.1f min, %.1f kcal burned (ID: %d)\n",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    exerciseName, exerciseType, duration, caloriesBurned, exerciseId);
            
            logArea.append(logEntry);
            logArea.setCaretPosition(logArea.getDocument().getLength());

            // Update status
            statusLabel.setText(String.format("✅ Exercise logged successfully! Calories burned: %.1f kcal", caloriesBurned));
            statusLabel.setForeground(new Color(0, 150, 0));

            // Clear fields for next entry
            clearFields();

        } catch (Exception e) {
            showError("Error logging exercise: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        exerciseNameField.setText("");
        exerciseTypeCombo.setSelectedItem("Running");
        durationSpinner.setValue(30.0);
        updateCaloriesEstimate();
        statusLabel.setText("Ready - Enter exercise details and click 'Log Exercise'");
        statusLabel.setForeground(Color.BLACK);
    }

    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setForeground(Color.RED);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Sets the profile ID for this exercise logger.
     * Updates the UI to reflect the new profile.
     */
    public void setProfile(int profileId) {
        if (profileId <= 0) {
            throw new IllegalArgumentException("Profile ID must be positive");
        }
        this.profileId = profileId;
        profileLabel.setText("Profile ID: " + profileId);
        statusLabel.setText("Profile set - Ready to log exercises for Profile " + profileId);
        statusLabel.setForeground(new Color(0, 100, 0));
    }

    /**
     * Gets the current profile ID.
     */
    public int getProfileId() {
        return profileId;
    }

    /**
     * Main method to launch the Exercise Logger UI.
     * For testing purposes and standalone execution.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            ExerciseLoggerUI exerciseLogger = new ExerciseLoggerUI();
            exerciseLogger.setProfile(1); // Set default profile for testing
            exerciseLogger.setVisible(true);
        });
    }
}