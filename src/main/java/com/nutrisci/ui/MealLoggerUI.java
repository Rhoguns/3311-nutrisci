package main.java.com.nutrisci.ui;


import main.java.com.nutrisci.model.Meal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class MealLoggerUI extends JFrame {
    private JTextField typeField, ingredientField, qtyField;
    private JButton addBtn, saveBtn;
    private JTextArea logArea;
    private Meal current;

    public MealLoggerUI() {
        super("Log Meal");
        setLayout(new BorderLayout());
        JPanel top = new JPanel(new GridLayout(3,2));
        top.add(new JLabel("Type:")); typeField=new JTextField(); top.add(typeField);
        top.add(new JLabel("Ingredient:")); ingredientField=new JTextField(); top.add(ingredientField);
        top.add(new JLabel("Qty (g):")); qtyField=new JTextField(); top.add(qtyField);
        add(top, BorderLayout.NORTH);

        logArea=new JTextArea(10,30); add(new JScrollPane(logArea), BorderLayout.CENTER);

        JPanel btnp=new JPanel();
        addBtn=new JButton("Add Ingredient"); saveBtn=new JButton("Save Meal");
        btnp.add(addBtn); btnp.add(saveBtn);
        add(btnp, BorderLayout.SOUTH);

        addBtn.addActionListener(new AddListener());
        saveBtn.addActionListener(new SaveListener());

        pack(); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private class AddListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (current==null) current=new Meal(typeField.getText(), new Date());
            String ing=ingredientField.getText(); double qty=Double.parseDouble(qtyField.getText());
            current.addIngredient(ing, qty);
            logArea.append(ing+": "+qty+"g\n");
            ingredientField.setText(""); qtyField.setText("");
        }
    }

    private class SaveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (current!=null) {
                logArea.append("TOTAL: "+current.getTotalCalories()+" kcal\n");
                current=null;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MealLoggerUI().setVisible(true));
    }
}

