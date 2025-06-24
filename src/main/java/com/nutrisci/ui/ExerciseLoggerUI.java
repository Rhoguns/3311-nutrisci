package main.java.com.nutrisci.ui;

import main.java.com.nutrisci.model.Exercise;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * A Swing UI for logging exercises.
 * <p>
 * Lets the user enter an exercise type and duration, then displays
 * each logged session’s summary including calories burned.
 */
public class ExerciseLoggerUI extends JFrame {
    /** Text field for the exercise type (e.g., "Running", "Cycling"). */
    private JTextField typeField;
    /** Text field for the exercise duration in minutes. */
    private JTextField durationField;
    /** Button to log the exercise session. */
    private JButton logBtn;
    /** Text area for displaying logged exercise summaries. */
    private JTextArea outputArea;
    /** The current Exercise object being logged. */
    private Exercise current;

    /**
     * Constructs the Exercise Logger UI, laying out input fields,
     * a log button, and an output text area.
     */
    public ExerciseLoggerUI() {
        super("Log Exercise");
        setLayout(new BorderLayout());

        // Top panel for inputs
        JPanel top = new JPanel(new GridLayout(2, 2));
        top.add(new JLabel("Type:"));
        typeField = new JTextField();
        top.add(typeField);

        top.add(new JLabel("Duration (min):"));
        durationField = new JTextField();
        top.add(durationField);

        add(top, BorderLayout.NORTH);

        // Center output area
        outputArea = new JTextArea(8, 30);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Bottom button
        logBtn = new JButton("Log Exercise");
        add(logBtn, BorderLayout.SOUTH);
        logBtn.addActionListener(new LogListener());

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    /**
     * Listener for the “Log Exercise” button.
     * Creates an Exercise, appends its summary, and clears inputs.
     */
    private class LogListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String type = typeField.getText();
            int duration = Integer.parseInt(durationField.getText());
            current = new Exercise(type, new Date(), duration);
            outputArea.append(current.summary() +
                "\n------------------------\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
            typeField.setText("");
            durationField.setText("");
        }
    }

    /**
     * Launches the Exercise Logger UI on the Event Dispatch Thread.
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExerciseLoggerUI().setVisible(true));
    }
}