package GUI;

import GUI.config;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class user_manager extends JFrame {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton updateUserButton, deleteUserButton, backButton;
    private JTextField pseudoField, roleField;

    public user_manager() {
        setTitle("Gestion des utilisateurs");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Table des utilisateurs
        tableModel = new DefaultTableModel(new String[]{"ID", "Email", "Pseudo", "Rôle"}, 0);
        userTable = new JTable(tableModel);
        loadUserData();
        add(new JScrollPane(userTable), BorderLayout.CENTER);

        // Panel bas
        JPanel bottomPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        pseudoField = new JTextField();
        roleField = new JTextField();
        updateUserButton = new JButton("Mettre à jour");
        deleteUserButton = new JButton("Supprimer utilisateur");
        backButton = new JButton("Retour");

        bottomPanel.add(new JLabel("Pseudo: "));
        bottomPanel.add(pseudoField);
        bottomPanel.add(new JLabel("Rôle: "));
        bottomPanel.add(roleField);
        bottomPanel.add(updateUserButton);
        bottomPanel.add(deleteUserButton);

        add(bottomPanel, BorderLayout.SOUTH);
        add(backButton, BorderLayout.NORTH);

        // Actions des boutons
        updateUserButton.addActionListener(e -> updateUser());
        deleteUserButton.addActionListener(e -> deleteUser());
        backButton.addActionListener(e -> goBack());

        setVisible(true);
    }

    private void loadUserData() {
        try (Connection conn = DriverManager.getConnection(config.link, config.login, config.password)) {
            String sql = "SELECT id, email, pseudo, role FROM users";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("id"), rs.getString("email"), rs.getString("pseudo"), rs.getString("role")});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données.");
        }
    }

    private void updateUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à mettre à jour.");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String pseudo = pseudoField.getText().trim();
        String role = roleField.getText().trim();

        if (pseudo.isEmpty() || role.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(config.link, config.login, config.password)) {
            String sql = "UPDATE users SET pseudo = ?, role = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, pseudo);
            stmt.setString(2, role);
            stmt.setInt(3, userId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Utilisateur mis à jour avec succès.");
            loadUserData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour de l'utilisateur.");
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à supprimer.");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        try (Connection conn = DriverManager.getConnection(config.link, config.login, config.password)) {
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Utilisateur supprimé avec succès.");
            loadUserData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'utilisateur.");
        }
    }

    private void goBack() {
        new home("");
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(user_manager::new);
    }
}
