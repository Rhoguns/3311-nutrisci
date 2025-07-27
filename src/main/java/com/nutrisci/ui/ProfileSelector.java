package com.nutrisci.ui;

import com.nutrisci.controller.ProfileController;
import com.nutrisci.dao.DAOFactory;
import com.nutrisci.dao.ProfileDAO;
import com.nutrisci.dao.ProfileDAOImpl;
import com.nutrisci.model.Profile;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ProfileSelector
extends JFrame {
    private ProfileDAO profileDao = DAOFactory.getProfileDAO();
    private JComboBox<Profile> cbProfiles = new JComboBox<>();
    private JButton btnNew = new JButton("New Profile");
    private JButton btnSelect = new JButton("Select");
    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ProfileSelector() {
        super("NutriSci – Select Profile");
        this.setDefaultCloseOperation(3);
        this.setLayout(new BorderLayout(10, 10));
        this.add((Component)new JLabel("Choose an existing profile or create a new one:", 0), "North");
        JPanel center = new JPanel(new FlowLayout(1, 8, 8));
        center.add(this.cbProfiles);
        center.add(this.btnNew);
        center.add(this.btnSelect);
        this.add((Component)center, "Center");
        this.refreshProfiles();
        this.btnNew.addActionListener(e -> {
            ProfileController controller = new ProfileController(new ProfileDAOImpl());
            ProfileUI profilePanel = new ProfileUI(controller);
            
            JFrame profileFrame = new JFrame("Create New Profile");
            profileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            profileFrame.add(profilePanel);
            profileFrame.pack();
            profileFrame.setLocationRelativeTo(this);
            
            profileFrame.addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosed(WindowEvent we) {
                    ProfileSelector.this.refreshProfiles();
                }
            });
            
            profileFrame.setVisible(true);
        });
        this.btnSelect.addActionListener(e -> {
            Profile p = (Profile)this.cbProfiles.getSelectedItem();
            if (p == null) {
                JOptionPane.showMessageDialog(this, "Please select a profile first.", "No Profile Selected", 2);
                return;
            }
            new MainDashboard(p.getId());
            this.dispose();
        });
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void refreshProfiles() {
        this.cbProfiles.removeAllItems();
        try {
            List<Profile> profiles = this.profileDao.findAll();
            this.cbProfiles.setRenderer((list, value, index, sel, focus) -> {
                JLabel lbl = new JLabel();
                if (value != null) {
                    lbl.setText(String.format("%d: %s (%s)", value.getId(), value.getName(), value.getDateOfBirth().format(this.df)));
                }
                return lbl;
            });
            for (Profile p : profiles) {
                this.cbProfiles.addItem(p);
            }
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading profiles:\n" + ex.getMessage(), "DB Error", 0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ProfileSelector();
        });
    }
}
