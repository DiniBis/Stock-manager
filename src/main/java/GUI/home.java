package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class home extends JFrame {
    private JButton userManagementButton;
    private JButton adminManagementButton;
    private JButton inventoryManagementButton;
    private JButton storeManagementButton;
    private JButton logoutButton;

    public home(String email) {
        setTitle("Accueil");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1, 10, 10));

        userManagementButton = new JButton("Gestion des utilisateurs");
        adminManagementButton = new JButton("Gestion des administrateurs");
        inventoryManagementButton = new JButton("Gestion de l'inventaire");
        storeManagementButton = new JButton("Gestion des magasins");
        logoutButton = new JButton("Déconnexion");

        add(userManagementButton);
        add(adminManagementButton);
        add(inventoryManagementButton);
        add(storeManagementButton);
        add(logoutButton);

        userManagementButton.addActionListener(e -> openUserManagement());
        adminManagementButton.addActionListener(e -> openAdminManagement());
        inventoryManagementButton.addActionListener(e -> openInventoryManagement());
        storeManagementButton.addActionListener(e -> openStoreManagement());
        logoutButton.addActionListener(e -> logout());

        setVisible(true);
    }

    private void openUserManagement() {
        new user_manager();
        dispose();
    }

    private void openAdminManagement() {
        new admin_manager();
        dispose();
    }

    private void openInventoryManagement() {
        new inventory_manager();
        dispose();
    }

    private void openStoreManagement() {
        new store_manager();
        dispose();
    }

    private void logout() {
        new login_form();
        dispose();
    }

}

