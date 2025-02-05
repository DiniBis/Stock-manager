package GUI;

import javax.swing.*;
import java.awt.*;

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

        // Définition des couleurs
        Color bgColor = new Color(30, 30, 30);
        Color btnColor = new Color(255, 140, 0);
        Color textColor = Color.WHITE;

        getContentPane().setBackground(bgColor);

        userManagementButton = createStyledButton("Gestion des utilisateurs", btnColor);
        adminManagementButton = createStyledButton("Gestion des administrateurs", btnColor);
        inventoryManagementButton = createStyledButton("Gestion de l'inventaire", btnColor);
        storeManagementButton = createStyledButton("Gestion des magasins", btnColor);
        logoutButton = createStyledButton("Déconnexion", btnColor);

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

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
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
