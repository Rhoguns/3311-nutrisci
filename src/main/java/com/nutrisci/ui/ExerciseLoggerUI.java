package main.java.com.nutrisci.ui;

import main.java.com.nutrisci.model.Exercise;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class ExerciseLoggerUI extends JFrame {
    private JTextField typeField, durationField;
    private JButton logBtn;
    private JTextArea outputArea;
    private Exercise current;

    public ExerciseLoggerUI() {
        super("Log Exercise");
        setLayout(new BorderLayout());
        JPanel top = new JPanel(new GridLayout(2,2));
        top.add(new JLabel("Type:"));
        typeField = new JTextField(); top.add(typeField);
        top.add(new JLabel("Duration (min):"));
        durationField = new JTextField(); top.add(durationField);
        add(top, BorderLayout.NORTH);

        outputArea = new JTextArea(8, 30);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        logBtn = new JButton("Log Exercise");
        add(logBtn, BorderLayout.SOUTH);
        logBtn.addActionListener(new LogListener());

        pack(); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private class LogListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String type = typeField.getText();
            int duration = Integer.parseInt(durationField.getText());
            current = new Exercise(type, new Date(), duration);
            outputArea.append(current.summary() + "\n" + "------------------------\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
            typeField.setText("");
            durationField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExerciseLoggerUI().setVisible(true));
    }
}
